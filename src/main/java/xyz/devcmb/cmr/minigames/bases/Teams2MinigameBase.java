package xyz.devcmb.cmr.minigames.bases;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Text;
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
                GameManager.teamColors.put(sortingPlayers.get(i), TextColor.color(0xFF0000));
            } else {
                BLUE.add(sortingPlayers.get(i));
                GameManager.teamColors.put(sortingPlayers.get(i), TextColor.color(0x0000FF));
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

            Component message = Component.text("A game of " + getName() + " is currently active, and you have been added as a spectator.").color(TextColor.color(0xFF0000));
            player.sendMessage(message);

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
            Component victoryMessage = Component.text("VICTORY").decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA000));
            Title victoryTitle = Title.title(victoryMessage, Component.empty(), Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10)));

            Component defeatMessage = Component.text("DEFEAT").decorate(TextDecoration.BOLD).color(TextColor.color(0xFF0000));
            Title defeatTitle = Title.title(defeatMessage, Component.empty(), Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10)));

            if (RED.isEmpty()) {
                BLUE.forEach(plr -> {
                    plr.showTitle(victoryTitle);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                });

                return 8;
            } else if (BLUE.isEmpty()) {
                RED.forEach(plr -> {
                    plr.showTitle(victoryTitle);
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
