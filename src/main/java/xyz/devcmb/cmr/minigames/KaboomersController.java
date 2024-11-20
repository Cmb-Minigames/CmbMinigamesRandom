package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.*;

import java.util.*;

public class KaboomersController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();

    public List<Block> redBlocks = new ArrayList<>();
    public List<Block> blueBlocks = new ArrayList<>();

    private final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;

    private BukkitRunnable timeDepreciation = null;

    public int timeLeft = 0;

    public KaboomersController(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");

        redTeam.setColor(ChatColor.RED);
        blueTeam.setColor(ChatColor.BLUE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
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
                allPlayers.get(i).setScoreboard(scoreboard);
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.RED);
            } else {
                BLUE.add(allPlayers.get(i));
                blueTeam.addEntry(allPlayers.get(i).getName());
                allPlayers.get(i).setScoreboard(scoreboard);
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.BLUE);
            }
        }

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");

        if (redSpawn == null || blueSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        String worldName = (String) mapData.get("worldName");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

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
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
        });

        GameManager.playersFrozen = true;
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());

        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setSaturation(0f);
                    Utilities.Countdown(player, 10);
                });

                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                    MusicBox.playTrack("kaboomers");
                    RED.forEach(player -> {
                        Map<?, List<?>> kit = Kits.kaboomers_kit;
                        Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
                    });

                    BLUE.forEach(player -> {
                        Map<?, List<?>> kit = Kits.kaboomers_kit;
                        Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
                    });

                    GameManager.playersFrozen = false;
                    GameManager.ingame = true;
                    timeLeft = 60 * 2;

                    timeDepreciation = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(timeLeft <= 0){
                                this.cancel();
                                endGame();
                                return;
                            }
                            timeLeft -= 1;
                        }
                    };
                    timeDepreciation.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20, 20);
                }, 20 * 10);
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 2);
    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redBlocks.clear();
        blueBlocks.clear();
        timeLeft = 0;

        if(timeDepreciation != null) timeDepreciation.cancel();

        Utilities.endGameResuable();
    }

    public void endGame(){
        GameManager.gameEnding = true;
        if(redBlocks.size() > blueBlocks.size()){
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(blueBlocks.size() > redBlocks.size()){
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 7);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Kaboomers is currently active, and you have been added as a spectator.");
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
            MinigameFlag.DISABLE_BLOCK_DROPS,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.DO_NOT_CONSUME_FIREWORKS
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");
        World world = Bukkit.getWorld(worldName);

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.kaboomers_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.kaboomers_kit, player, Material.BLUE_CONCRETE);
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
                    CMScoreboardManager.scoreboards.get("kaboomers").getScoreboard(player),
                    scoreboard
                )
        );
    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of(
            StarSource.KILL, 2,
            StarSource.WIN, 20
        );
    }

    @Override
    public String getId() {
        return "kaboomers";
    }

    @Override
    public String getName() {
        return "Kaboomers";
    }

    @Override
    public String getDescription() {
        return "Claim area around the map as your own by shooting a fireball at it, which will claim up to a 3 by 3 by 3 cube, whichever team has the most claimed by the end wins";
    }
}
