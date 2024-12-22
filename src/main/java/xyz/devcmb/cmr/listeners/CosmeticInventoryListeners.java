package xyz.devcmb.cmr.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.cosmetics.CosmeticInventory;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Database;

import java.util.Map;
import java.util.Objects;

/**
 * A class for the cosmetic inventory listeners
 */
public class CosmeticInventoryListeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (Objects.equals(meta.getItemModel(), CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("cosmetic_inventory"))) {
            CosmeticInventory cosmeticInventory = CosmeticManager.playerInventories.get(event.getPlayer());
            cosmeticInventory.openInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        CosmeticInventory cosmeticInventory = CosmeticManager.playerInventories.get(player);
        if (event.getView().getTitle().equals("Cosmetic Inventory")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if(item == null) return;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            if (meta.getItemName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory")) {
                cosmeticInventory.page = "inventory";
                cosmeticInventory.openInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            } else if (meta.getItemName().equals(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates")) {
                cosmeticInventory.page = "crates";
                cosmeticInventory.openInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            } else if(meta.getItemName().equals(ChatColor.AQUA + ChatColor.BOLD.toString() + "Shop")){
                cosmeticInventory.page = "shop";
                cosmeticInventory.openInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }

            if(item.getType() == Material.CHEST){
                if(cosmeticInventory.page.equals("crates")){
                    Map<String, Object> crate = CrateManager.getFromDisplayName(meta.getItemName());
                    if (crate == null) return;
                    cosmeticInventory.rollCrateInventory(crate.get("name").toString());
                    Database.removeCrate(player, crate.get("name").toString());
                } else if(cosmeticInventory.page.equals("shop")){
                    Map<String, Object> crate = CrateManager.getFromDisplayName(meta.getItemName());
                    if (crate == null) return;

                    if(event.getClick().isLeftClick()){
                        if(Database.getUserStars(player) >= (int) crate.get("shop_price")){
                            Database.setUserStars(player, Database.getUserStars(player) - (int) crate.get("shop_price"));
                            Database.giveCrate(player, crate.get("name").toString());
                            cosmeticInventory.page = "crates";
                            cosmeticInventory.openInventory();
                            player.sendMessage(ChatColor.GREEN + "You have purchased a " + ChatColor.GOLD + crate.get("display_name") + ChatColor.GREEN + " crate!");
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have enough stars to purchase this crate.");
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                    } else if(event.getClick().isRightClick()) {
                        cosmeticInventory.openPreviewWindow(crate.get("name").toString());
                    }
                }
            }

            if(cosmeticInventory.page.equals("inventory")){
                if(meta.getItemName().equals(ChatColor.YELLOW + "Next Page")){
                    cosmeticInventory.inventoryPage++;
                    cosmeticInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                } else if(meta.getItemName().equals(ChatColor.YELLOW + "Previous Page")){
                    cosmeticInventory.inventoryPage--;
                    cosmeticInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                } else if(item.getType() == Material.LEATHER_HORSE_ARMOR){
                    Map<String, Object> cosmetic = CosmeticManager.getFromDisplayName(ChatColor.stripColor(meta.getItemName()));
                    if(cosmetic == null) return;
                    Database.equipCosmetic(player, cosmetic.get("name").toString());
                    CosmeticManager.equipHat(player);
                    cosmeticInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                }
            } else if(cosmeticInventory.page.equals("crates")){
                if(meta.getItemName().equals(ChatColor.YELLOW + "Next Page")){
                    cosmeticInventory.cratePage++;
                    cosmeticInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                } else if(meta.getItemName().equals(ChatColor.YELLOW + "Previous Page")){
                    cosmeticInventory.cratePage--;
                    cosmeticInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }
            }
        } else if (event.getView().getTitle().equals("Crate")){
            event.setCancelled(true);
        } else if (event.getView().getTitle().equals("Crate Preview")){
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            if(event.getCurrentItem().getType() == Material.ARROW){
                cosmeticInventory.openInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        }
    }

    @EventHandler
    public void onArmorClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getInventory().getHelmet() != null) {
            event.setCancelled(true);
        }
    }
}
