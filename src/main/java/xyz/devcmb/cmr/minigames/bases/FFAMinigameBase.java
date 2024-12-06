package xyz.devcmb.cmr.minigames.bases;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.StarSource;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract public class FFAMinigameBase {
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();
    protected Location spawnLocation = null;
    protected World world = null;
    protected Map<String, Object> mapData = null;

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
            player.setSaturation(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        });
    }

    public void stop() {
        players.clear();
        allPlayers.clear();
        spawnLocation = null;
        mapData = null;
        world = null;

        Utilities.endGameResuable();
    }


    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(spawnLocation);
            player.sendMessage(ChatColor.RED + "A game of " + getName() + " is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }


    public Number playerLeave(Player player) {
        players.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (players.isEmpty()) ? 0 : null;
        } else {
            if(players.size() == 1){
                Player winner = players.getFirst();
                Database.addUserStars(winner, getStarSources().get(StarSource.WIN));
                winner.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
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

    protected void endGame(){
        GameManager.gameEnding = true;

        Player winner = players.getFirst();
        Database.addUserStars(winner, getStarSources().get(StarSource.WIN));

        allPlayers.forEach(player -> {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);

            player.sendTitle(winner == player ?
                    ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY" :
                    ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT",
            "", 5, 80, 10);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::stop, 20 * 8);
    }

    protected abstract String getName();
    protected abstract Map<StarSource, Integer> getStarSources();
}
