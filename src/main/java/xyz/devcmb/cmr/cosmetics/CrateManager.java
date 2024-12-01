package xyz.devcmb.cmr.cosmetics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A class for managing crates
 */
public class CrateManager {
    public static Map<String, ItemStack> crates = new HashMap<>();
    public static Map<String, Map<String, Object>> crateData = new HashMap<>();

    /**
     * Registers all crates
     */
    public static void registerAllCrates(){
        Map<String, Map<String, Object>> crateMap = Database.getAllCrates();
        crateMap.forEach(CrateManager::createCrate);
    }

    /**
     * Registers a crate
     * @param name The name of the crate
     * @param crate The crate data
     */
    private static void createCrate(String name, Map<String, Object> crate){
        if(crate == null){
            CmbMinigamesRandom.LOGGER.warning("Crate " + name + " is null.");
            return;
        }

        ItemStack crateItem = new ItemStack(Material.CHEST);
        ItemMeta meta = crateItem.getItemMeta();
        if(meta == null) return;
        meta.setItemName(crate.get("display_name").toString());
        Map<String, Number> raritySet = Database.getRaritySet((Integer) crate.get("rarity_set"));
        if(raritySet == null) {
            CmbMinigamesRandom.LOGGER.warning("Rarity set not found for crate " + name);
            return;
        }

        meta.setLore(List.of(
            ChatColor.WHITE + "Common: " + ChatColor.WHITE + raritySet.get("common") + "%",
            ChatColor.GREEN + "Uncommon: " + ChatColor.WHITE + raritySet.get("uncommon") + "%",
            ChatColor.BLUE + "Rare: " + ChatColor.WHITE + raritySet.get("rare") + "%",
            ChatColor.DARK_PURPLE + "Epic: " + ChatColor.WHITE + raritySet.get("epic") + "%",
            ChatColor.GOLD + "Legendary: " + ChatColor.WHITE + raritySet.get("legendary") + "%",
            ChatColor.RED + "Mythic: " + ChatColor.WHITE + raritySet.get("mythic") + "%"
        ));
        crateItem.setItemMeta(meta);

        crates.put(name, crateItem);
        crateData.put(name, crate);
    }

    /**
     * Gives a crate item to a player
     * @param player The player to give the crate to
     * @param name The name of the crate
     */
    public static void giveCrate(Player player, String name){
        ItemStack cosmetic = crates.get(name);
        if(cosmetic == null) player.sendMessage("❓ " + ChatColor.RED + "That crate does not exist.");
        player.getInventory().addItem(cosmetic);
    }

    /**
     * Rolls a crate for a player
     * @param player The player to roll the crate for
     * @param name The name of the crate
     * @return The cosmetic that was rolled
     */
    public static String rollCrate(Player player, String name) {
        Map<String, Object> crate = crateData.get(name);
        if (crate == null) {
            player.sendMessage("❓ " + ChatColor.RED + "That crate does not exist.");
            return null;
        }

        Map<String, Number> raritySet = Database.getRaritySet((Integer) crate.get("rarity_set"));
        if (raritySet == null) {
            player.sendMessage("❓ " + ChatColor.RED + "Rarity set not found.");
            return null;
        }

        String selectedCosmetic = rollRandomCosmeticFromCrate(name);
        Database.giveCosmetic(player, selectedCosmetic);
        return selectedCosmetic;
    }

    /**
     * Get a crate from its display name
     * @param display_name The display name of the crate
     * @return The crate data
     */
    public static Map<String, Object> getFromDisplayName(String display_name) {
        for (Map.Entry<String, Map<String, Object>> entry : crateData.entrySet()) {
            if (entry.getValue().get("display_name").equals(display_name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Rolls a random cosmetic from a crate
     * @param crate The name of the crate
     * @return The cosmetic name that was rolled
     */
    public static String rollRandomCosmeticFromCrate(String crate){
        Map<String, Object> crateData = CrateManager.crateData.get(crate);
        if(crateData == null) return null;

        Map<String, Number> raritySet = Database.getRaritySet((Integer) crateData.get("rarity_set"));
        if(raritySet == null) return null;

        String selectedRarity = rollRandomRarity(raritySet);

        List<String> cosmeticNames = List.of(crateData.get("cosmetics").toString().split("\\|"));
        List<String> selectedCosmetics = cosmeticNames.stream().filter(s -> {
            Map<String, Object> cosmetic = CosmeticManager.cosmeticData.get(s);
            if(cosmetic == null) return false;
            return cosmetic.get("rarity").equals(selectedRarity);
        }).toList();

        if(selectedCosmetics.isEmpty()){
            selectedCosmetics = cosmeticNames.stream().filter(s -> {
                Map<String, Object> cosmetic = CosmeticManager.cosmeticData.get(s);
                return cosmetic != null;
            }).toList();
        }

        return selectedCosmetics.get(new Random().nextInt(selectedCosmetics.size()));
    }

    /**
     * Rolls a random rarity
     * @param raritySet The rarity set to roll from
     * @return The rarity that was rolled
     */
    public static String rollRandomRarity(Map<String, Number> raritySet) {
        double common = raritySet.get("common").doubleValue();
        double uncommon = raritySet.get("uncommon").doubleValue();
        double rare = raritySet.get("rare").doubleValue();
        double epic = raritySet.get("epic").doubleValue();
        double legendary = raritySet.get("legendary").doubleValue();
        double mythic = raritySet.get("mythic").doubleValue();

        double total = common + uncommon + rare + epic + legendary + mythic;
        double randomValue = Math.random() * total;

        double currentThreshold = 0;
        if (randomValue < (currentThreshold += mythic)) return "mythic";
        if (randomValue < (currentThreshold += legendary)) return "legendary";
        if (randomValue < (currentThreshold += epic)) return "epic";
        if (randomValue < (currentThreshold += rare)) return "rare";
        if (randomValue < currentThreshold + uncommon) return "uncommon";
        return "common";
    }
}
