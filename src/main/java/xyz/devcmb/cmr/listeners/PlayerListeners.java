package xyz.devcmb.cmr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.scoreboards.ScoreboardManager;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Location spawnPoint = new Location(Bukkit.getWorld("pregame"), -26.5, -43.5, -18);
        Player player = event.getPlayer();
        ScoreboardManager.initialize(player);
        GameManager.playerConnect(player);
        player.getInventory().clear();
        player.teleport(spawnPoint);
        player.setRespawnLocation(spawnPoint);
    }
}
