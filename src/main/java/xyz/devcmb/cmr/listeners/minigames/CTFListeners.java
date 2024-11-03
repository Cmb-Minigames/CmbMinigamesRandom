package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;

public class CTFListeners implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        CaptureTheFlagController ctfController = (CaptureTheFlagController) GameManager.getMinigameByName("Capture The Flag");
        if(ctfController == null || GameManager.currentMinigame != ctfController) return;
        ctfController.handlePlayerMove(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        CaptureTheFlagController ctfController = (CaptureTheFlagController) GameManager.getMinigameByName("Capture The Flag");
        if(ctfController == null || GameManager.currentMinigame != ctfController) return;
        if(event.getDamager() instanceof Player player && event.getEntity() instanceof Player target){
            if(ctfController.RED.contains(player) && ctfController.RED.contains(target)){
                event.setCancelled(true);
            } else if(ctfController.BLUE.contains(player) && ctfController.BLUE.contains(target)){
                event.setCancelled(true);
            }
        }
    }
}
