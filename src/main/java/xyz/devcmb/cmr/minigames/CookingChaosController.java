package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CookingChaosController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;
    private BukkitRunnable boneMealChestRefill;

    public Integer redScore = 0;
    public Integer blueScore = 0;

    public CookingChaosController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");
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
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.RED);
            } else {
                BLUE.add(allPlayers.get(i));
                blueTeam.addEntry(allPlayers.get(i).getName());
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.BLUE);
            }
        }

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueSpawn");

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


        Map<String, Object> redBarrier = (Map<String, Object>) mapData.get("redBarrier");
        Map<String, Object> blueBarrier = (Map<String, Object>) mapData.get("blueBarrier");

        if (redBarrier == null || blueBarrier == null) {
            CmbMinigamesRandom.LOGGER.warning("Barrier points are not defined.");
            return;
        }

        Map<String, Object> redBarrierFrom = (Map<String, Object>) redBarrier.get("from");
        Map<String, Object> redBarrierTo = (Map<String, Object>) redBarrier.get("to");

        Map<String, Object> blueBarrierFrom = (Map<String, Object>) blueBarrier.get("from");
        Map<String, Object> blueBarrierTo = (Map<String, Object>) blueBarrier.get("to");

        if (redBarrierFrom == null || redBarrierTo == null || blueBarrierFrom == null || blueBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Barrier points are not defined.");
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

        RED.forEach(player -> {
            player.teleport(redSpawnLocation);
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        BLUE.forEach(player -> {
            player.teleport(blueSpawnLocation);
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        List<Map<String, Map<String, Number>>> bonemealChests = (List<Map<String, Map<String, Number>>>) mapData.get("boneMealChests");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);
            Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.AIR);

            RED.forEach(player -> {
                Map<?, List<?>> kit = Kits.cookingchaos_kit;
                Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
            });

            BLUE.forEach(player -> {
                Map<?, List<?>> kit = Kits.cookingchaos_kit;
                Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
            });

            boneMealChestRefill = new BukkitRunnable() {
                @Override
                public void run() {
                    bonemealChests.forEach(chest -> {
                        Map<String, Number> chestLoc = chest.get("location");
                        Location chestLocation = new Location(
                            world,
                            chestLoc.get("x").doubleValue(),
                            chestLoc.get("y").doubleValue(),
                            chestLoc.get("z").doubleValue()
                        );

                        BlockState blockData = chestLocation.getBlock().getState();
                        if(!(blockData instanceof Chest chestData)) {
                            CmbMinigamesRandom.LOGGER.warning("Chest at " + chestLocation + " is not a chest.");
                        } else {
                            chestData.getInventory().clear();
                            for (int i = 1; i < 3; i++){
                                chestData.getInventory().setItem(11 + i, new ItemStack(Material.BONE_MEAL, 64));
                            }

                            chestData.getInventory().setItem(10, new ItemStack(Material.WHEAT_SEEDS, 32));
                            chestData.getInventory().setItem(15, new ItemStack(Material.MELON_SEEDS, 32));
                        }
                    });
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Crop chests have been refilled!");
                }
            };

            boneMealChestRefill.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 60);
        }, 10 * 20);
    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);
        boneMealChestRefill.cancel();
        boneMealChestRefill = null;
        redScore = 0;
        blueScore = 0;

        Utilities.endGameResuable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of cooking chaos is currently active, and you have been added as a spectator.");
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
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.CANNOT_TRAMPLE_FARMLAND,
            MinigameFlag.CANNOT_PLACE_BLOCKS
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {

    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {

    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of();
    }

    @Override
    public String getId() {
        return "cookingchaos";
    }

    @Override
    public String getName() {
        return "Cooking Chaos";
    }

    @Override
    public String getDescription() {
        return "Race for resources to cook for your animal patreons where pvp is enabled, you can sabotage the other team or play it safe and get your own resources. The team with the most customers fed in 10 minutes wins. ";
    }
}
