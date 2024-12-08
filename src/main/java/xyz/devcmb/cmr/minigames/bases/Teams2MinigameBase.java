package xyz.devcmb.cmr.minigames.bases;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.StarSource;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A base class for team-based minigames
 */
abstract public class Teams2MinigameBase {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();

    public World world = null;
    protected Map<String, Object> mapData = null;
    public Location redSpawn = null;
    public Location blueSpawn = null;

    /**
     * Start the minigame
     */
    @SuppressWarnings("unchecked")
    public void start() {
        Utilities.gameStartReusable();
        List<Player> sortingPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(sortingPlayers);
        allPlayers.addAll(Bukkit.getOnlinePlayers());

        RED.clear();
        BLUE.clear();

        for (int i = 0; i < sortingPlayers.size(); i++) {
            if (i % 2 == 0) {
                RED.add(sortingPlayers.get(i));
                GameManager.teamColors.put(sortingPlayers.get(i), ChatColor.RED);
            } else {
                BLUE.add(sortingPlayers.get(i));
                GameManager.teamColors.put(sortingPlayers.get(i), ChatColor.BLUE);
            }
        }


        mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        redSpawn = Utilities.getLocationFromConfig(mapData, world, "redSpawn");
        blueSpawn = Utilities.getLocationFromConfig(mapData, world, "blueSpawn");
    }

    /**
     * Stop the minigame
     */
    public void stop() {
        RED.clear();
        BLUE.clear();
        world = null;
        redSpawn = null;
        blueSpawn = null;

        Utilities.endGameResuable();
    }

    /**
     * Handle player join
     * @param event The event
     */
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(redSpawn);
            player.sendMessage(ChatColor.RED + "A game of " + getName() + " is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    /**
     * Handle player leave
     * @param player The player
     * @return The number of players left
     */
    public Number playerLeave(Player player) {
        RED.remove(player);
        BLUE.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (RED.isEmpty() && BLUE.isEmpty()) ? 0 : null;
        } else {
            if (RED.isEmpty()) {
                BLUE.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                });

                return 8;
            } else if (BLUE.isEmpty()) {
                RED.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                });

                return 8;
            }
        }

        return null;
    }

    protected abstract String getName();
    protected abstract Map<StarSource, Integer> getStarSources();
}
