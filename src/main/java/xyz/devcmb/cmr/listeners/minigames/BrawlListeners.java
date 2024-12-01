package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.BrawlController;

/**
 * A class for listeners that are specific to the Brawl minigame
 */
public class BrawlListeners implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        BrawlController brawlController = (BrawlController) GameManager.getMinigameByName("Brawl");
        if(brawlController == null || GameManager.currentMinigame != brawlController) return;

        if(event.getBlock().getType() == Material.CHEST) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        BrawlController brawlController = (BrawlController) GameManager.getMinigameByName("Brawl");
        if(brawlController == null || GameManager.currentMinigame != brawlController) return;

        event.blockList().removeIf(block -> block.getType() == Material.CHEST);
    }
}
