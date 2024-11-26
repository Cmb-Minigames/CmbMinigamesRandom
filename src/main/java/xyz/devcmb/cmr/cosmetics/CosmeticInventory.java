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
import java.util.Objects;

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
    public static Integer inventoryPage = 1;
    public static Integer cratePage = 1;
    public static void openInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, 45, "Cosmetic Inventory");
        switch (page) {
            case "inventory" -> {
                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta selectedMeta = selected.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.setItemName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory");
                selectedMeta.setLore(List.of(ChatColor.GRAY + "Selected"));
                selected.setItemMeta(selectedMeta);

                inventory.setItem(0, selected);

                ItemStack crates = new ItemStack(Material.CHEST);
                ItemMeta cratesMeta = crates.getItemMeta();
                if (cratesMeta == null) return;
                cratesMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates");
                crates.setItemMeta(cratesMeta);

                inventory.setItem(1, crates);

                ItemStack shop = new ItemStack(Material.LANTERN);
                ItemMeta shopMeta = shop.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.setItemName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Shop");
                shop.setItemMeta(shopMeta);

                inventory.setItem(2, shop);

                // Border
                for (int i = 9; i < 19; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }
                inventory.setItem(26, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                inventory.setItem(27, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                for (int i = 35; i < 45; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }

                List<String> userCosmetics = Database.getUserCosmetics(player);
                int itemsPerPage = 14;
                int startIndex = (inventoryPage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, userCosmetics.size());
                int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

                for (int i = startIndex; i < endIndex && i - startIndex < slots.length; i++) {
                    ItemStack cosmeticItem = CosmeticManager.cosmetics.get(userCosmetics.get(i)).clone();
                    ItemMeta cosmeticMeta = cosmeticItem.getItemMeta();
                    if (cosmeticMeta == null) return;
                    if (Database.isCosmeticEquipped(player, userCosmetics.get(i))) {
                        if (cosmeticMeta.getLore() == null) return;
                        List<String> lore = new ArrayList<>(cosmeticMeta.getLore());
                        lore.add(ChatColor.WHITE + "Equipped");
                        cosmeticMeta.setLore(lore);
                    }

                    cosmeticItem.setItemMeta(cosmeticMeta);
                    inventory.setItem(slots[i - startIndex], cosmeticItem);
                }

                if (inventoryPage > 1) {
                    ItemStack previousPage = new ItemStack(Material.ARROW);
                    ItemMeta previousPageMeta = previousPage.getItemMeta();
                    if (previousPageMeta != null) {
                        previousPageMeta.setItemName(ChatColor.YELLOW + "Previous Page");
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(36, previousPage);
                }

                if (endIndex < userCosmetics.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setItemName(ChatColor.YELLOW + "Next Page");
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(44, nextPage);
                }
            }
            case "crates" -> {
                ItemStack inventoryItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta inventoryItemMeta = (SkullMeta) inventoryItem.getItemMeta();
                if (inventoryItemMeta == null) return;
                inventoryItemMeta.setOwningPlayer(player);
                inventoryItemMeta.setItemName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory");
                inventoryItem.setItemMeta(inventoryItemMeta);

                inventory.setItem(0, inventoryItem);

                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta selectedMeta = selected.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates");
                selectedMeta.setLore(List.of(ChatColor.GRAY + "Selected"));
                selected.setItemMeta(selectedMeta);

                inventory.setItem(1, selected);

                ItemStack shop = new ItemStack(Material.LANTERN);
                ItemMeta shopMeta = shop.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.setItemName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Shop");
                shop.setItemMeta(shopMeta);

                inventory.setItem(2, shop);

                for (int i = 9; i < 19; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }
                inventory.setItem(26, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                inventory.setItem(27, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                for (int i = 35; i < 45; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }

                List<String> userCrates = Database.getUserCrates(player);
                int itemsPerPage = 14;
                int startIndex = (cratePage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, userCrates.size());
                int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

                for (int i = startIndex; i < endIndex && i - startIndex < slots.length; i++) {
                    ItemStack crateItem = CrateManager.crates.get(userCrates.get(i));
                    if (crateItem != null) {
                        inventory.setItem(slots[i - startIndex], crateItem);
                    }
                }

                if (cratePage > 1) {
                    ItemStack previousPage = new ItemStack(Material.ARROW);
                    ItemMeta previousPageMeta = previousPage.getItemMeta();
                    if (previousPageMeta != null) {
                        previousPageMeta.setItemName(ChatColor.YELLOW + "Previous Page");
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(36, previousPage);
                }

                if (endIndex < userCrates.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setItemName(ChatColor.YELLOW + "Next Page");
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(44, nextPage);
                }
            }
            case "shop" -> {
                ItemStack inventoryItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta inventoryItemMeta = (SkullMeta) inventoryItem.getItemMeta();
                if (inventoryItemMeta == null) return;
                inventoryItemMeta.setOwningPlayer(player);
                inventoryItemMeta.setItemName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Inventory");
                inventoryItem.setItemMeta(inventoryItemMeta);

                inventory.setItem(0, inventoryItem);

                ItemStack crates = new ItemStack(Material.CHEST);
                ItemMeta selectedMeta = crates.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Crates");
                crates.setItemMeta(selectedMeta);

                inventory.setItem(1, crates);

                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta shopMeta = selected.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.setItemName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Shop");
                shopMeta.setLore(List.of(ChatColor.GRAY + "Selected"));
                selected.setItemMeta(shopMeta);

                inventory.setItem(2, selected);

                for (int i = 9; i < 19; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }
                inventory.setItem(26, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                inventory.setItem(27, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                for (int i = 35; i < 45; i++) {
                    inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
                }

                Map<String, Map<String, Object>> allCrates = Database.getAllCrates();
                int itemsPerPage = 14;
                int startIndex = (CosmeticInventory.cratePage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, allCrates.size());
                int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

                int index = 0;
                for (Map.Entry<String, Map<String, Object>> entry : allCrates.entrySet()) {
                    if (index >= startIndex && index < endIndex) {
                        if (((Integer) entry.getValue().get("in_shop")) != 1) return;
                        ItemStack crateItem = CrateManager.crates.get(entry.getKey());
                        if (crateItem != null) {
                            ItemStack newItem = crateItem.clone();
                            ItemMeta newItemMeta = newItem.getItemMeta();
                            if (newItemMeta == null) return;
                            List<String> lore = new ArrayList<>(List.of(
                                    ChatColor.WHITE + "Price: 🌟" + ChatColor.YELLOW + entry.getValue().get("shop_price"),
                                    ChatColor.AQUA + "Left click to purchase!",
                                    ChatColor.AQUA + "Right click to preview!",
                                    ""
                            ));

                            lore.addAll(Objects.requireNonNull(newItemMeta.getLore()));
                            newItemMeta.setLore(lore);

                            newItem.setItemMeta(newItemMeta);

                            inventory.setItem(slots[index - startIndex], newItem);
                        }
                    }
                    if (((Integer) entry.getValue().get("in_shop")) == 1) index++;
                }

                if (CosmeticInventory.cratePage > 1) {
                    ItemStack previousPage = new ItemStack(Material.ARROW);
                    ItemMeta previousPageMeta = previousPage.getItemMeta();
                    if (previousPageMeta != null) {
                        previousPageMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(36, previousPage);
                }

                if (endIndex < allCrates.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(44, nextPage);
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

        inventory.setItem(4, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
        inventory.setItem(22, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));

        player.openInventory(inventory);

        List<ItemStack> cosmetics = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            String cosmeticName = CrateManager.rollRandomCosmeticFromCrate(crate);
            ItemStack cosmeticItem = CosmeticManager.cosmetics.get(cosmeticName);
            if (cosmeticItem != null) {
                cosmetics.add(cosmeticItem);
            }
        }
        ItemStack cosmetic = CosmeticManager.cosmetics.get(CrateManager.rollCrate(player, crate));
        cosmetics.add(cosmetic);
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
                    player.sendMessage(ChatColor.GOLD + "You have rolled a " + ChatColor.RESET + Objects.requireNonNull(cosmetic.getItemMeta()).getItemName());
                    Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                        page = "inventory";
                        openInventory(player);
                    }, 40);
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

    public static void openPreviewWindow(Player player, String crate){
        Map<String, Object> crateData = CrateManager.crateData.get(crate);
        if(crateData == null) return;

        Inventory inventory = Bukkit.createInventory(player, 45, "Crate Preview");

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }
        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }
        for (int i = 9; i < 36; i += 9) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            inventory.setItem(i + 8, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if(backMeta == null) return;
        backMeta.setItemName(ChatColor.YELLOW + "Back");
        back.setItemMeta(backMeta);

        inventory.setItem(0, back);

        List<String> cosmeticNames = List.of(crateData.get("cosmetics").toString().split("\\|"));
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < cosmeticNames.size() && i < slots.length; i++) {
            ItemStack cosmeticItem = CosmeticManager.cosmetics.get(cosmeticNames.get(i));
            if (cosmeticItem != null) {
                inventory.setItem(slots[i], cosmeticItem);
            }
        }

        player.openInventory(inventory);
    }
}
