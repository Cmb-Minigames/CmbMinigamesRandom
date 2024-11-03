package xyz.devcmb.cmr.listeners;

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
    }
}
