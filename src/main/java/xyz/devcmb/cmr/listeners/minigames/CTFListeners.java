package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(checkProtections(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(checkProtections(event.getBlock())) event.setCancelled(true);
    }

    private boolean checkProtections(Block block) {
        CaptureTheFlagController ctfController = (CaptureTheFlagController) GameManager.getMinigameByName("Capture The Flag");
        if(ctfController == null || GameManager.currentMinigame != ctfController) return false;
        Location redFlagLocation = ctfController.redFlagEntity.getLocation();
        Location blueFlagLocation = ctfController.blueFlagEntity.getLocation();
        Location blockLocation = block.getLocation();

        int blockPlacingDistance = 5;
        return blockLocation.distance(redFlagLocation) <= blockPlacingDistance || blockLocation.distance(blueFlagLocation) <= blockPlacingDistance;
    }
}
