package xyz.devcmb.cmr.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeleportersController implements Minigame {
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();

    @Override
    public void start() {
        Utilities.gameStartReusable();

        players.addAll(Bukkit.getOnlinePlayers());
        allPlayers.addAll(Bukkit.getOnlinePlayers());
    }

    @Override
    public void stop() {
        players.clear();
        allPlayers.clear();

        Utilities.endGameResuable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> spawn = (Map<String, Object>) mapData.get("spawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) spawn.get("x")).doubleValue(), ((Number) spawn.get("y")).doubleValue(), ((Number) spawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Teleporters is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        return null;
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.CANNOT_BREAK_BLOCKS,
            MinigameFlag.CANNOT_PLACE_BLOCKS,
            MinigameFlag.USE_CUSTOM_RESPAWN,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.DISABLE_BLOCK_DROPS
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
        return "teleporters";
    }

    @Override
    public String getName() {
        return "Teleporters";
    }

    @Override
    public String getDescription() {
        return "A minigame where you are given a stack of pearls and have to stay on the platform. At the start of the game, the amount of lives will be selected, which can either be 1, 5, 10, 15, or 20. Over time, items used to push other players off will spawn around the map. Last person standing wins!";
    }
}
