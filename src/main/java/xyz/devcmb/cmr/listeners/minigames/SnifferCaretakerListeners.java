package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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

    private final Map<Material, Integer> snifferRequestedItems = Map.of(
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

        Item itemDrop = event.getItemDrop();
        Material material = itemDrop.getItemStack().getType();

        if (controller.RED.contains(event.getPlayer()) && itemDrop.getLocation().distance(controller.redSniffer.getLocation()) > 7.0) return;
        if (controller.BLUE.contains(event.getPlayer()) && itemDrop.getLocation().distance(controller.blueSniffer.getLocation()) > 7.0) return;

        if (snifferRequestedItems.containsKey(material)) {
            int happinessIncrease = snifferRequestedItems.get(material) * itemDrop.getItemStack().getAmount();
            if (controller.RED.contains(event.getPlayer())) {
                controller.redSnifferHappiness = Math.clamp(controller.redSnifferHappiness + happinessIncrease, 0, 1000);
                event.getPlayer().sendMessage(ChatColor.RED + "[Red Sniffer] " + ChatColor.RESET + (happinessIncrease >= 50 ? "This makes me VERY happy!" : "This makes me happy!"));
            }
            if (controller.BLUE.contains(event.getPlayer())) {
                controller.blueSnifferHappiness = Math.clamp(controller.blueSnifferHappiness + happinessIncrease, 0, 1000);
                event.getPlayer().sendMessage(ChatColor.BLUE + "[Blue Sniffer] " + ChatColor.RESET + (happinessIncrease >= 50 ? "This makes me VERY happy!" : "This makes me happy!"));
            }
            itemDrop.remove();
        } else {
            if (controller.RED.contains(event.getPlayer())) event.getPlayer().sendMessage(ChatColor.RED + "[Red Sniffer] I don't want that!");
            if (controller.BLUE.contains(event.getPlayer())) event.getPlayer().sendMessage(ChatColor.BLUE + "[Blue Sniffer] " + ChatColor.RED + "I don't want that!");
        }
    }
}
