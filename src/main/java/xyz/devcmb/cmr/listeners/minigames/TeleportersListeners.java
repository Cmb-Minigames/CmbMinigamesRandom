package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.TeleportersController;

public class TeleportersListeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        TeleportersController controller = (TeleportersController) GameManager.getMinigameByName("Teleporters");
        if (controller == null || GameManager.currentMinigame != controller) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
                event.getPlayer().setCooldown(Material.ENDER_PEARL, 0);
            }
        }
    }
}
