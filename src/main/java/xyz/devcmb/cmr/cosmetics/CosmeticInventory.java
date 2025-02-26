package xyz.devcmb.cmr.cosmetics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Database;

import java.util.*;

/**
 * A class for managing the cosmetic inventory
 */
public class CosmeticInventory {
    private final Player player;
    private static final ItemStack empty = new ItemStack(Material.ECHO_SHARD);
    public CosmeticInventory(Player plr){
        player = plr;

        ItemMeta meta = empty.getItemMeta();
        if(meta == null) return;
        meta.setHideTooltip(true);
        meta.setItemModel(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("empty_slot"));

        empty.setItemMeta(meta);
    }

    /**
     * Gives the player the inventory item
     */
    public void giveInventoryItem(){
        ItemStack inventoryItem = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = inventoryItem.getItemMeta();
        if(meta == null) return;
        meta.displayName(Component.text("Cosmetic Inventory").color(Colors.BLUE).decoration(TextDecoration.ITALIC, false));
        meta.setItemModel(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("cosmetic_inventory"));
        inventoryItem.setItemMeta(meta);

        player.getInventory().addItem(inventoryItem);
    }

    public String page = "inventory";
    public Integer inventoryPage = 1;
    public Integer cratePage = 1;

    /**
     * Opens the cosmetic inventory for the player
     */
    public void openInventory(){
        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("Cosmetic Inventory"));
        switch (page) {
            case "inventory" -> {
                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta selectedMeta = selected.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.displayName(Component.text("Inventory").color(Colors.GREEN).decorate(TextDecoration.BOLD));
                selectedMeta.lore(List.of(Component.text("Selected").color(Colors.GRAY)));
                selected.setItemMeta(selectedMeta);

                inventory.setItem(0, selected);

                ItemStack crates = new ItemStack(Material.CHEST);
                ItemMeta cratesMeta = crates.getItemMeta();
                if (cratesMeta == null) return;
                cratesMeta.displayName(Component.text("Crates").color(Colors.GOLD).decorate(TextDecoration.BOLD));
                crates.setItemMeta(cratesMeta);

                inventory.setItem(1, crates);

                ItemStack shop = new ItemStack(Material.LANTERN);
                ItemMeta shopMeta = shop.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.displayName(Component.text("Shop").color(Colors.AQUA).decorate(TextDecoration.BOLD));
                shop.setItemMeta(shopMeta);

                inventory.setItem(2, shop);

                for(int i = 3; i < 9; i++){
                    inventory.setItem(i, empty);
                }

                // Border
                for (int i = 9; i < 18; i++) {
                    inventory.setItem(i, empty);
                }
                for (int i = 45; i < 54; i++) {
                    inventory.setItem(i, empty);
                }

                List<String> userCosmetics = Database.getUserCosmetics(player);

                userCosmetics.sort(Collections.reverseOrder((cosmetic1, cosmetic2) -> {
                    Map<String, Object> cosmeticData1 = CosmeticManager.cosmeticData.get(cosmetic1);
                    Map<String, Object> cosmeticData2 = CosmeticManager.cosmeticData.get(cosmetic2);

                    if (cosmeticData1 == null || cosmeticData2 == null) {
                        return 0;
                    }

                    int rarityOrder1 = CosmeticManager.getRarityOrder((String) cosmeticData1.get("rarity"));
                    int rarityOrder2 = CosmeticManager.getRarityOrder((String) cosmeticData2.get("rarity"));
                    int rarityComparison = Integer.compare(rarityOrder1, rarityOrder2);

                    if (rarityComparison != 0) {
                        return rarityComparison;
                    }

                    return cosmetic1.compareTo(cosmetic2);
                }));

                int itemsPerPage = 27;
                int startIndex = (inventoryPage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, userCosmetics.size());
                int[] slots = {18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

                for (int i = startIndex; i < endIndex && i - startIndex < slots.length; i++) {
                    ItemStack cosmeticItem = CosmeticManager.cosmetics.get(userCosmetics.get(i)).clone();
                    ItemMeta cosmeticMeta = cosmeticItem.getItemMeta();
                    if (cosmeticMeta == null) return;
                    if (Database.isCosmeticEquipped(player, userCosmetics.get(i))) {
                        if (cosmeticMeta.lore() == null) return;
                        List<Component> lore = new ArrayList<>(Objects.requireNonNull(cosmeticMeta.lore()));
                        lore.add(Component.text("Equipped").color(Colors.WHITE));
                        cosmeticMeta.lore(lore);
                    }

                    cosmeticItem.setItemMeta(cosmeticMeta);
                    inventory.setItem(slots[i - startIndex], cosmeticItem);
                }

                if (inventoryPage > 1) {
                    ItemStack previousPage = new ItemStack(Material.ARROW);
                    ItemMeta previousPageMeta = previousPage.getItemMeta();
                    if (previousPageMeta != null) {
                        previousPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        previousPageMeta.displayName(Component.text("Previous Page").color(Colors.YELLOW));
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(45, previousPage);
                }

                if (endIndex < userCosmetics.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        nextPageMeta.displayName(Component.text("Next Page").color(Colors.YELLOW));
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(53, nextPage);
                }
            }
            case "crates" -> {
                ItemStack inventoryItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta inventoryItemMeta = (SkullMeta) inventoryItem.getItemMeta();
                if (inventoryItemMeta == null) return;
                inventoryItemMeta.setOwningPlayer(player);
                inventoryItemMeta.displayName(Component.text("Inventory").color(Colors.GREEN).decorate(TextDecoration.BOLD));
                inventoryItem.setItemMeta(inventoryItemMeta);

                inventory.setItem(0, inventoryItem);

                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta selectedMeta = selected.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.displayName(Component.text("Crates").color(Colors.GOLD).decorate(TextDecoration.BOLD));
                selectedMeta.lore(List.of(Component.text("Selected").color(Colors.GRAY)));
                selected.setItemMeta(selectedMeta);

                inventory.setItem(1, selected);

                ItemStack shop = new ItemStack(Material.LANTERN);
                ItemMeta shopMeta = shop.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.displayName(Component.text("Shop").color(Colors.AQUA).decorate(TextDecoration.BOLD));
                shop.setItemMeta(shopMeta);

                inventory.setItem(2, shop);

                for(int i = 3; i < 9; i++){
                    inventory.setItem(i, empty);
                }

                for (int i = 9; i < 18; i++) {
                    inventory.setItem(i, empty);
                }

                for (int i = 45; i < 54; i++) {
                    inventory.setItem(i, empty);
                }

                List<String> userCrates = Database.getUserCrates(player);
                int itemsPerPage = 27;
                int startIndex = (cratePage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, userCrates.size());
                int[] slots = {18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

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
                        previousPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        previousPageMeta.displayName(Component.text("Previous Page").color(Colors.YELLOW));
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(45, previousPage);
                }

                if (endIndex < userCrates.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        nextPageMeta.displayName(Component.text("Next Page").color(Colors.YELLOW));
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(53, nextPage);
                }
            }
            case "shop" -> {
                ItemStack inventoryItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta inventoryItemMeta = (SkullMeta) inventoryItem.getItemMeta();
                if (inventoryItemMeta == null) return;
                inventoryItemMeta.setOwningPlayer(player);
                inventoryItemMeta.displayName(Component.text("Inventory").color(Colors.GREEN).decorate(TextDecoration.BOLD));
                inventoryItem.setItemMeta(inventoryItemMeta);

                inventory.setItem(0, inventoryItem);

                ItemStack crates = new ItemStack(Material.CHEST);
                ItemMeta selectedMeta = crates.getItemMeta();
                if (selectedMeta == null) return;
                selectedMeta.displayName(Component.text("Crates").color(Colors.GOLD).decorate(TextDecoration.BOLD));
                crates.setItemMeta(selectedMeta);

                inventory.setItem(1, crates);

                ItemStack selected = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta shopMeta = selected.getItemMeta();
                if (shopMeta == null) return;
                shopMeta.displayName(Component.text("Shop").color(Colors.AQUA).decorate(TextDecoration.BOLD));
                shopMeta.lore(List.of(Component.text("Selected").color(Colors.GRAY)));
                selected.setItemMeta(shopMeta);

                inventory.setItem(2, selected);

                for(int i = 3; i < 9; i++){
                    inventory.setItem(i, empty);
                }

                for (int i = 9; i < 18; i++) {
                    inventory.setItem(i, empty);
                }

                for (int i = 45; i < 54; i++) {
                    inventory.setItem(i, empty);
                }

                Map<String, Map<String, Object>> allCrates = Database.getAllCrates();
                int itemsPerPage = 27;
                int startIndex = (cratePage - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, allCrates.size());
                int[] slots = {18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

                int index = 0;
                for (Map.Entry<String, Map<String, Object>> entry : allCrates.entrySet()) {
                    if (index >= startIndex && index < endIndex) {
                        if (((Integer) entry.getValue().get("in_shop")) != 1) return;
                        ItemStack crateItem = CrateManager.crates.get(entry.getKey());
                        if (crateItem != null) {
                            ItemStack newItem = crateItem.clone();
                            ItemMeta newItemMeta = newItem.getItemMeta();
                            if (newItemMeta == null) return;
                            List<Component> lore = new ArrayList<>(List.of(
                                    Component.text("Price: 🌟").append(Component.text(entry.getValue().get("shop_price").toString())).color(Colors.YELLOW).decoration(TextDecoration.ITALIC, false),
                                    Component.text("Left click to purchase!").color(Colors.AQUA).decoration(TextDecoration.ITALIC, false),
                                    Component.text("Right click to preview!").color(Colors.AQUA).decoration(TextDecoration.ITALIC, false),
                                    Component.empty()
                            ));

                            lore.addAll(Objects.requireNonNull(newItemMeta.lore()));
                            newItemMeta.lore(lore);

                            newItem.setItemMeta(newItemMeta);
                            inventory.setItem(slots[index - startIndex], newItem);
                        }
                    }
                    if (((Integer) entry.getValue().get("in_shop")) == 1) index++;
                }

                if (cratePage > 1) {
                    ItemStack previousPage = new ItemStack(Material.ARROW);
                    ItemMeta previousPageMeta = previousPage.getItemMeta();
                    if (previousPageMeta != null) {
                        previousPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        previousPageMeta.displayName(Component.text("Previous Page").color(Colors.YELLOW));
                        previousPage.setItemMeta(previousPageMeta);
                    }
                    inventory.setItem(45, previousPage);
                }

                if (endIndex < allCrates.size()) {
                    ItemStack nextPage = new ItemStack(Material.ARROW);
                    ItemMeta nextPageMeta = nextPage.getItemMeta();
                    if (nextPageMeta != null) {
                        nextPageMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
                        nextPageMeta.displayName(Component.text("Next Page").color(Colors.YELLOW));
                        nextPage.setItemMeta(nextPageMeta);
                    }
                    inventory.setItem(53, nextPage);
                }
            }
        }
        player.openInventory(inventory);
    }

    /**
     * Rolls a crate using {@link CrateManager#rollCrate} and opens its inventory
     * @param crate The crate to roll
     */
    public void rollCrateInventory(String crate){
        Map<String, Object> crateData = CrateManager.crateData.get(crate);
        if(crateData == null) return;

        Inventory inventory = Bukkit.createInventory(player, 27, Component.text("Crate"));
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

                    Component rolled = Component.text("You have rolled a ").color(Colors.GOLD)
                            .append(Objects.requireNonNull(Objects.requireNonNull(cosmetic.getItemMeta()).displayName()).decorate(TextDecoration.BOLD));

                    player.sendMessage(rolled);
                    Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                        page = "inventory";
                        openInventory();
                    }, 40);
                    return;
                }

                for (int i = 9; i < 17; i++) {
                    inventory.setItem(i, inventory.getItem(i + 1));
                }

                ItemStack currentCosmetic = cosmetics.get(index);
                if(currentCosmetic == null) {
                    CmbMinigamesRandom.LOGGER.warning("Cosmetic is null.");
                    return;
                }

                inventory.setItem(17, currentCosmetic);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);

                index++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 3);
    }

    /**
     * Opens the preview window for a crate
     * @param crate The crate to preview
     */
    public void openPreviewWindow(String crate){
        Map<String, Object> crateData = CrateManager.crateData.get(crate);
        if(crateData == null) return;

        Inventory inventory = Bukkit.createInventory(player, 45, Component.text("Crate Preview"));

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, empty);
        }
        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, empty);
        }
        for (int i = 9; i < 36; i += 9) {
            inventory.setItem(i, empty);
            inventory.setItem(i + 8, empty);
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if(backMeta == null) return;
        backMeta.setItemModel(new NamespacedKey("cmbminigames", "functional/arrow_slot"));
        backMeta.itemName(Component.text("Back").color(Colors.YELLOW));
        back.setItemMeta(backMeta);

        inventory.setItem(0, back);

        List<String> cosmeticNames = new ArrayList<>(List.of(crateData.get("cosmetics").toString().split("\\|")));
        cosmeticNames.sort((cosmetic1, cosmetic2) -> {
            int rarityOrder1 = CosmeticManager.getRarityOrder((String) CosmeticManager.cosmeticData.get(cosmetic1).get("rarity"));
            int rarityOrder2 = CosmeticManager.getRarityOrder((String) CosmeticManager.cosmeticData.get(cosmetic2).get("rarity"));
            return Integer.compare(rarityOrder1, rarityOrder2);
        });

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < cosmeticNames.size() && i < slots.length; i++) {
            ItemStack cosmeticItem = CosmeticManager.cosmetics.get(cosmeticNames.get(i));
            if (cosmeticItem != null) {
                inventory.setItem(slots[i], cosmeticItem);
            }
        }

        player.openInventory(inventory);
    }

    /**
     * Get a glass pane material by checking if it has a ChatColor in its name
     * @param rarity The string of the rarity
     * @deprecated Isn't worth making it work with paper changes
     */
    @Deprecated
    public Material chatColorToGlassPane(String rarity) {
        if (rarity.contains(ChatColor.WHITE.toString())) {
            return Material.WHITE_STAINED_GLASS_PANE;
        } else if (rarity.contains(ChatColor.GREEN.toString())) {
            return Material.GREEN_STAINED_GLASS_PANE;
        } else if (rarity.contains(ChatColor.BLUE.toString())) {
            return Material.LIGHT_BLUE_STAINED_GLASS_PANE;
        } else if (rarity.contains(ChatColor.DARK_PURPLE.toString())) {
            return Material.PURPLE_STAINED_GLASS_PANE;
        } else if (rarity.contains(ChatColor.GOLD.toString())) {
            return Material.ORANGE_STAINED_GLASS_PANE;
        } else if (rarity.contains(ChatColor.RED.toString())) {
            return Material.RED_STAINED_GLASS_PANE;
        } else {
            return Material.GRAY_STAINED_GLASS_PANE;
        }
    }
}
