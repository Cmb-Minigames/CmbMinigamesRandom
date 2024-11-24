package xyz.devcmb.cmr.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.cosmetics.CosmeticInventory;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;

public class CosmeticInventoryListeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ECHO_SHARD) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (meta.getCustomModelData() == CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("cosmetic_inventory").intValue()) {
            CosmeticInventory.openInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Cosmetic Inventory")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta == null) return;
            if (meta.getItemName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory")) {
                CosmeticInventory.page = "inventory";
                CosmeticInventory.openInventory((Player) event.getWhoClicked());
            } else if (meta.getItemName().equals(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates")) {
                CosmeticInventory.page = "crates";
                CosmeticInventory.openInventory((Player) event.getWhoClicked());
            }
        }
    }
}
