package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SnifferCaretakerController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;

    public Entity redSniffer;
    public Entity blueSniffer;

    public SnifferCaretakerController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");
    }
    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        Utilities.gameStartReusable();
        List<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(allPlayers);

        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);

        for (int i = 0; i < allPlayers.size(); i++) {
            if (i % 2 == 0) {
                RED.add(allPlayers.get(i));
                redTeam.addEntry(allPlayers.get(i).getName());
            } else {
                BLUE.add(allPlayers.get(i));
                blueTeam.addEntry(allPlayers.get(i).getName());
            }
        }

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");

        if (redSpawn == null || blueSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        Map<String, Object> redBarrierFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("redTeamBarrier")).get("from");
        Map<String, Object> redBarrierTo = (Map<String, Object>)((Map<String, Object>) mapData.get("redTeamBarrier")).get("to");

        if (redBarrierFrom == null || redBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Red barrier points are not defined.");
            return;
        }

        Location redBarrierFromLocation = new Location(
                world,
                ((Number) redBarrierFrom.get("x")).doubleValue(),
                ((Number) redBarrierFrom.get("y")).doubleValue(),
                ((Number) redBarrierFrom.get("z")).doubleValue()
        );

        Location redBarrierToLocation = new Location(
                world,
                ((Number) redBarrierTo.get("x")).doubleValue(),
                ((Number) redBarrierTo.get("y")).doubleValue(),
                ((Number) redBarrierTo.get("z")).doubleValue()
        );

        Map<String, Object> blueBarrierFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("blueTeamBarrier")).get("from");
        Map<String, Object> blueBarrierTo = (Map<String, Object>)((Map<String, Object>) mapData.get("blueTeamBarrier")).get("to");

        if (blueBarrierFrom == null || blueBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Blue barrier points are not defined.");
            return;
        }

        Location blueBarrierFromLocation = new Location(
                world,
                ((Number) blueBarrierFrom.get("x")).doubleValue(),
                ((Number) blueBarrierFrom.get("y")).doubleValue(),
                ((Number) blueBarrierFrom.get("z")).doubleValue()
        );

        Location blueBarrierToLocation = new Location(
                world,
                ((Number) blueBarrierTo.get("x")).doubleValue(),
                ((Number) blueBarrierTo.get("y")).doubleValue(),
                ((Number) blueBarrierTo.get("z")).doubleValue()
        );

        Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.BARRIER);
        Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.BARRIER);

        Map<String, Object> redSnifferSpawn = (Map<String, Object>) mapData.get("redTeamSnifferSpawn");
        Map<String, Object> blueSnifferSpawn = (Map<String, Object>) mapData.get("blueTeamSnifferSpawn");

        if (redSnifferSpawn == null || blueSnifferSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Sniffer spawns are not defined.");
            return;
        }

        Location redSnifferSpawnLocation = new Location(
                world,
                ((Number) redSnifferSpawn.get("x")).doubleValue(),
                ((Number) redSnifferSpawn.get("y")).doubleValue(),
                ((Number) redSnifferSpawn.get("z")).doubleValue()
        );

        Location blueSnifferSpawnLocation = new Location(
                world,
                ((Number) blueSnifferSpawn.get("x")).doubleValue(),
                ((Number) blueSnifferSpawn.get("y")).doubleValue(),
                ((Number) blueSnifferSpawn.get("z")).doubleValue()
        );

        redSniffer = world.spawnEntity(redSnifferSpawnLocation, EntityType.SNIFFER);
        blueSniffer = world.spawnEntity(blueSnifferSpawnLocation, EntityType.SNIFFER);
        redSniffer.setInvulnerable(true);
        blueSniffer.setInvulnerable(true);

        Location redSpawnLocation = new Location(
                world,
                ((Number) redSpawn.get("x")).doubleValue(),
                ((Number) redSpawn.get("y")).doubleValue(),
                ((Number) redSpawn.get("z")).doubleValue()
        );

        Location blueSpawnLocation = new Location(
                world,
                ((Number) blueSpawn.get("x")).doubleValue(),
                ((Number) blueSpawn.get("y")).doubleValue(),
                ((Number) blueSpawn.get("z")).doubleValue()
        );

        RED.forEach(player -> {
            player.teleport(Utilities.findValidLocation(redSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Map<?, List<?>> kit = Kits.sniffercaretaker_kit;
            RED.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                player.setSaturation(0);
                player.setHealth(20);
            });
            BLUE.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                player.setSaturation(0);
                player.setHealth(20);
            });

            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);
            Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.AIR);
        }, 20 * 10);
    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);

        redSniffer = null;
        blueSniffer = null;

        Utilities.endGameResuable();
    }
    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Sniffer Caretaker is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        RED.remove(player);
        BLUE.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (RED.isEmpty() && BLUE.isEmpty()) ? 0 : null;
        } else {
            if(RED.isEmpty()){
                GameManager.gameEnding = true;
                BLUE.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                });
                return 7;
            } else if(BLUE.isEmpty()){
                GameManager.gameEnding = true;
                RED.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                });

                return 7;
            }
        }

        return null;
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");
        World world = Bukkit.getWorld(worldName);

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)blueSpawn.get("x")).doubleValue(), ((Number)blueSpawn.get("y")).doubleValue(), ((Number)blueSpawn.get("z")).doubleValue()));
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
            player,
            CMScoreboardManager.mergeScoreboards(
                CMScoreboardManager.scoreboards.get("sniffercaretaker").getScoreboard(player),
                scoreboard
            )
        );
    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of(
            StarSource.KILL, 2,
            StarSource.WIN, 20,
            StarSource.OBJECTIVE, 1
        );
    }

    @Override
    public String getId() {
        return "sniffercaretaker";
    }

    @Override
    public String getName() {
        return "Sniffer Caretaker";
    }

    @Override
    public String getDescription() {
        return "Keep your team’s sniffer alive by giving it food, dirt, and torchflower seeds stolen from the other team. You win if the other team’s sniffer is not taken care of enough and dies.";
    }
}
