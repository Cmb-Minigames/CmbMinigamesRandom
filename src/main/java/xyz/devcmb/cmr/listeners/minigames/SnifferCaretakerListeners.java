package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnifferCaretakerListeners implements Listener {
    private final List<Material> breakableBlocks = List.of(
        Material.DIRT,
        Material.GRASS_BLOCK,
        Material.HAY_BLOCK,
        Material.WHEAT,
        Material.COCOA,
        Material.ACACIA_LOG,
        Material.ACACIA_LEAVES,
        Material.COAL_ORE,
        Material.DEEPSLATE_COAL_ORE
    );

    private final Map<Material, Number> snifferRequestedItems = Map.of(
        Material.DIRT, 2,
        Material.WHEAT, 5,
        Material.HAY_BLOCK, 50,
        Material.COOKIE, 25,
        Material.MUTTON, 10,
        Material.COOKED_MUTTON, 70
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        if (!breakableBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;
        // if it isn't obvious enough these are temporary messages, make it do things when i implement the happiness

        if (controller.RED.contains(event.getPlayer()) && event.getItemDrop().getLocation().distance(controller.redSniffer.getLocation()) > 5.0) return;
        if (controller.BLUE.contains(event.getPlayer()) && event.getItemDrop().getLocation().distance(controller.blueSniffer.getLocation()) > 5.0) return;

        if (snifferRequestedItems.containsKey(event.getItemDrop().getItemStack().getType())) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Sniffer says THANK YOU FOR YOUR GOODIES");
            event.getItemDrop().remove();
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Sniffer says i do NOT want that");
            event.getItemDrop().remove();
        }
    }
}
