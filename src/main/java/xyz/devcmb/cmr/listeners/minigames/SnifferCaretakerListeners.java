package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;

import java.util.ArrayList;
import java.util.List;

public class SnifferCaretakerListeners implements Listener {
    private final List<Material> breakableBlocks = List.of(
        Material.DIRT,
        Material.WHEAT,
        Material.COCOA
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        if (!breakableBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
    }
}
