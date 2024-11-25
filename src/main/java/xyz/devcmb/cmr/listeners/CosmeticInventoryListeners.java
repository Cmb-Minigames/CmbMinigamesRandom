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
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Database;

import java.util.Map;

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
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("Cosmetic Inventory")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if(item == null) return;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            if (meta.getItemName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory")) {
                CosmeticInventory.page = "inventory";
                CosmeticInventory.openInventory(player);
            } else if (meta.getItemName().equals(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates")) {
                CosmeticInventory.page = "crates";
                CosmeticInventory.openInventory(player);
            } else if(item.getType() == Material.CHEST) {
                Map<String, Object> crate = CrateManager.getFromDisplayName(meta.getItemName());
                if (crate == null) return;
                CosmeticInventory.rollCrateInventory(player, crate.get("name").toString());
                Database.removeCrate(player, crate.get("name").toString());
            }

            if(CosmeticInventory.page.equals("inventory")){
                if(meta.getItemName().equals(ChatColor.YELLOW + "Next Page")){
                    CosmeticInventory.inventoryPage++;
                    CosmeticInventory.openInventory(player);
                } else if(meta.getItemName().equals(ChatColor.YELLOW + "Previous Page")){
                    CosmeticInventory.inventoryPage--;
                    CosmeticInventory.openInventory(player);
                } else if(item.getType() == Material.LEATHER_HORSE_ARMOR){
                    Map<String, Object> cosmetic = CosmeticManager.getFromDisplayName(ChatColor.stripColor(meta.getItemName()));
                    if(cosmetic == null) return;
                    Database.equipCosmetic(player, cosmetic.get("name").toString());
                    CosmeticManager.equipHat(player);
                    CosmeticInventory.openInventory(player);
                }
            } else if(CosmeticInventory.page.equals("crates")){
                if(meta.getItemName().equals(ChatColor.YELLOW + "Next Page")){
                    CosmeticInventory.cratePage++;
                    CosmeticInventory.openInventory(player);
                } else if(meta.getItemName().equals(ChatColor.YELLOW + "Previous Page")){
                    CosmeticInventory.cratePage--;
                    CosmeticInventory.openInventory(player);
                }
            }
        } else if (event.getView().getTitle().equals("Crate")){
            event.setCancelled(true);
        }
    }
}
