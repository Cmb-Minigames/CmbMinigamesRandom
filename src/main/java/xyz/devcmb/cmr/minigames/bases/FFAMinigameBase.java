package xyz.devcmb.cmr.minigames.bases;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.minigames.StarSource;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A base class for Free For All minigames
 */
abstract public class FFAMinigameBase {
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();
    protected Location spawnLocation = null;
    protected World world = null;
    protected Map<String, Object> mapData = null;

    /**
     * Start the minigame
     */
    @SuppressWarnings("unchecked")
    public void start() {
        Utilities.gameStartReusable();
        mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        players.addAll(Bukkit.getOnlinePlayers());
        allPlayers.addAll(Bukkit.getOnlinePlayers());

        String worldName = MapLoader.LOADED_MAP;
        world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        spawnLocation = Utilities.getLocationFromConfig(mapData, world, "spawn");

        players.forEach(player -> {
            player.teleport(spawnLocation);
            Fade.fadePlayer(player, 0, 0, 40);
            player.setSaturation(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
        });
    }

    /**
     * Stop the minigame
     */
    public void stop() {
        players.clear();
        allPlayers.clear();
        spawnLocation = null;
        mapData = null;
        world = null;

        Utilities.endGameResuable();
    }

    /**
     * Handle player join
     * @param event The event
     */
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(spawnLocation);
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
        players.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (players.isEmpty()) ? 0 : null;
        } else {
            if(players.size() == 1){
                Player winner = players.getFirst();
                Database.addUserStars(winner, getStarSources().get(StarSource.WIN));
                Title victoryTitle = Title.title(Component.text("VICTORY").decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA000)), Component.empty(), Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10)));

                winner.showTitle(victoryTitle);
                winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                winner.getInventory().clear();
                winner.setGameMode(GameMode.SPECTATOR);

                return 8;
            } else if(players.isEmpty()){
                return 0;
            }
        }

        return null;
    }

    /**
     * End the game
     */
    protected void endGame(){
        GameManager.gameEnding = true;

        Player winner = players.getFirst();
        Database.addUserStars(winner, getStarSources().get(StarSource.WIN));

        allPlayers.forEach(player -> {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);

            Component victoryMessage = Component.text("VICTORY").decorate(TextDecoration.BOLD).color(TextColor.color(0xFFA000));
            Title victoryTitle = Title.title(victoryMessage, Component.empty(), Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10)));

            Component defeatMessage = Component.text("DEFEAT").decorate(TextDecoration.BOLD).color(TextColor.color(0xFF0000));
            Title defeatTitle = Title.title(defeatMessage, Component.empty(), Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10)));

            player.showTitle(winner == player ? victoryTitle : defeatTitle);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::stop, 20 * 8);
    }

    protected abstract String getName();
    protected abstract Map<StarSource, Integer> getStarSources();
}
