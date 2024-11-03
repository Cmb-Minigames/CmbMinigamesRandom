package xyz.devcmb.cmr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.scoreboards.ScoreboardManager;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        ScoreboardManager.initialize(event.getPlayer());
        GameManager.playerConnect(event.getPlayer());
        event.getPlayer().getInventory().clear();
        event.getPlayer().teleport(new Location(Bukkit.getWorld("pregame"), -26.5, -43.5, -18));
    }
}
