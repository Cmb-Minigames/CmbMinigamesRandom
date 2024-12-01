package xyz.devcmb.cmr.cosmetics;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for managing cosmetics
 */
public class CosmeticManager {
    public static Map<String, ItemStack> cosmetics = new HashMap<>();
    public static Map<String, Map<String, Object>> cosmeticData = new HashMap<>();
    public static Map<Player, CosmeticInventory> playerInventories = new HashMap<>();

    /**
     * Registers all cosmetics
     */
    public static void registerAllCosmetics(){
        Map<String, Map<String, Object>> cosmeticMap = Database.getAllCosmetics();
        cosmeticMap.forEach(CosmeticManager::createCosmetic);
    }

    /**
     * Registers the cosmetic inventory for a player
     * @param player The player to register for
     */
    public static void playerJoin(Player player){
        playerInventories.put(player, new CosmeticInventory(player));
    }

    /**
     * Creates a cosmetic
     * @param name The name of the cosmetic
     * @param cosmetic The cosmetic data
     */
    private static void createCosmetic(String name, Map<String, Object> cosmetic){
        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        int customModelData = (int) cosmetic.get("custom_model_data");
        meta.setCustomModelData(customModelData);
        String display_name = (String) cosmetic.get("display_name");
        String rarity = (String) cosmetic.get("rarity");

        CmbMinigamesRandom.LOGGER.info("Registering cosmetic: " + name + " with rarity " + rarity);

        ChatColor rarityColor = switch (rarity.toLowerCase()) {
            case "uncommon" -> ChatColor.GREEN;
            case "rare" -> ChatColor.BLUE;
            case "epic" -> ChatColor.DARK_PURPLE;
            case "legendary" -> ChatColor.GOLD;
            case "mythic" -> ChatColor.RED;
            default -> ChatColor.WHITE;
        };

        meta.setItemName(rarityColor + display_name);
        meta.setLore(List.of(
            ChatColor.GRAY + (String) cosmetic.get("description"),
            rarityColor + rarity
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        item.setItemMeta(meta);
        cosmetics.put(name, item);
        cosmeticData.put(name, cosmetic);
    }

    /**
     * Gets a cosmetic by its display name
     * @param display_name The display name of the cosmetic
     * @return The cosmetic data
     */
    public static Map<String, Object> getFromDisplayName(String display_name){
        for(Map.Entry<String, Map<String, Object>> entry : cosmeticData.entrySet()){
            if(entry.getValue().get("display_name").equals(display_name)){
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the rarity order of a cosmetic
     * @param rarity The rarity of the cosmetic
     * @return The rarity order
     */
    public static int getRarityOrder(String rarity){
        return switch (rarity.toLowerCase()) {
            case "common" -> 0;
            case "uncommon" -> 1;
            case "rare" -> 2;
            case "epic" -> 3;
            case "legendary" -> 4;
            case "mythic" -> 5;
            default -> -1;
        };
    }

    /**
     * Equips a player's equipped cosmetic
     * @param player The player to equip the cosmetic for
     */
    public static void equipHat(Player player){
        String equipped = Database.getEquipped(player);
        if(equipped == null) return;
        ItemStack cosmetic = cosmetics.get(equipped);
        if(cosmetic == null) player.sendMessage("❓ " + ChatColor.RED + "That cosmetic does not exist.");
        player.getInventory().setHelmet(cosmetic);
    }

    /**
     * Gives a player a cosmetic item
     * @param player The player to give the cosmetic to
     * @param name The name of the cosmetic
     */
    public static void giveCosmetic(Player player, String name){
        ItemStack cosmetic = cosmetics.get(name);
        if(cosmetic == null) player.sendMessage("❓ " + ChatColor.RED + "That cosmetic does not exist.");
        player.getInventory().addItem(cosmetic);
    }
}
