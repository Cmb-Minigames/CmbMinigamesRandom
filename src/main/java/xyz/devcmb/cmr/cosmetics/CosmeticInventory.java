package xyz.devcmb.cmr.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticInventory {
    public static void giveInventoryItem(Player player){
        ItemStack inventoryItem = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = inventoryItem.getItemMeta();
        if(meta == null) return;
        meta.setItemName("Cosmetic Inventory");
        meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("cosmetic_inventory").intValue());
        inventoryItem.setItemMeta(meta);

        player.getInventory().addItem(inventoryItem);
    }

    public static String page = "inventory";
    public static void openInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, 45, "Cosmetic Inventory");
        if(page.equals("inventory")){
            ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta selectedMeta = selected.getItemMeta();
            if(selectedMeta == null) return;
            selectedMeta.setItemName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory");
            selectedMeta.setLore(List.of(ChatColor.GRAY + "Selected"));
            selected.setItemMeta(selectedMeta);

            inventory.setItem(0, selected);

            ItemStack crates = new ItemStack(Material.CHEST);
            ItemMeta cratesMeta = crates.getItemMeta();
            if(cratesMeta == null) return;
            cratesMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates");
            crates.setItemMeta(cratesMeta);

            inventory.setItem(1, crates);

            // Border
            for(int i = 9; i < 19; i++){
                inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            }
            inventory.setItem(26, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            inventory.setItem(27, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            for(int i = 35; i < 45; i++){
                inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            }

            List<String> userCosmetics = Database.getUserCosmetics(player);
            int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            for (int i = 0; i < userCosmetics.size() && i < slots.length; i++) {
                ItemStack cosmeticItem = CosmeticManager.cosmetics.get(userCosmetics.get(i));
                if (cosmeticItem != null) {
                    inventory.setItem(slots[i], cosmeticItem);
                }
            }
        } else if (page.equals("crates")) {
            ItemStack inventoryItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta inventoryItemMeta = (SkullMeta) inventoryItem.getItemMeta();
            if(inventoryItemMeta == null) return;
            inventoryItemMeta.setOwningPlayer(player);
            inventoryItemMeta.setItemName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory");
            inventoryItem.setItemMeta(inventoryItemMeta);

            inventory.setItem(0, inventoryItem);

            ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta selectedMeta = selected.getItemMeta();
            if(selectedMeta == null) return;
            selectedMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates");
            selectedMeta.setLore(List.of(ChatColor.GRAY + "Selected"));
            selected.setItemMeta(selectedMeta);

            inventory.setItem(1, selected);
            for(int i = 9; i < 19; i++){
                inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            }
            inventory.setItem(26, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            inventory.setItem(27, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            for(int i = 35; i < 45; i++){
                inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            }

            List<String> userCrates = Database.getUserCrates(player);
            int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            for (int i = 0; i < userCrates.size() && i < slots.length; i++) {
                ItemStack crateItem = CrateManager.crates.get(userCrates.get(i));
                if (crateItem != null) {
                    inventory.setItem(slots[i], crateItem);
                }
            }
        }
        player.openInventory(inventory);
    }

    public static void rollCrateInventory(Player player, String crate){
        Map<String, Object> crateData = CrateManager.crateData.get(crate);
        if(crateData == null) return;

        Inventory inventory = Bukkit.createInventory(player, 27, "Crate");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        player.openInventory(inventory);

        List<ItemStack> cosmetics = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            String cosmeticName = CrateManager.rollRandomCosmeticFromCrate(crate);
            ItemStack cosmeticItem = CosmeticManager.cosmetics.get(cosmeticName);
            if (cosmeticItem != null) {
                cosmetics.add(cosmeticItem);
            }
        }
        cosmetics.add(CosmeticManager.cosmetics.get(CrateManager.rollCrate(player, crate)));
        for (int i = 0; i < 4; i++) {
            String cosmeticName = CrateManager.rollRandomCosmeticFromCrate(crate);
            ItemStack cosmeticItem = CosmeticManager.cosmetics.get(cosmeticName);
            if (cosmeticItem != null) {
                cosmetics.add(cosmeticItem);
            }
        }

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index == cosmetics.size()) {
                    this.cancel();
                    return;
                }

                for (int i = 9; i < 17; i++) {
                    inventory.setItem(i, inventory.getItem(i + 1));
                }

                inventory.setItem(17, cosmetics.get(index));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
                index++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 3);
    }
}
