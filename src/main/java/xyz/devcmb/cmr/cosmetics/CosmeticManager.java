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

public class CosmeticManager {
    public static Map<String, ItemStack> cosmetics = new HashMap<>();
    public static Map<String, Map<String, Object>> cosmeticData = new HashMap<>();

    public static void registerAllCosmetics(){
        Map<String, Map<String, Object>> cosmeticMap = Database.getAllCosmetics();
        cosmeticMap.forEach(CosmeticManager::createCosmetic);
    }

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

    public static Map<String, Object> getFromDisplayName(String display_name){
        for(Map.Entry<String, Map<String, Object>> entry : cosmeticData.entrySet()){
            if(entry.getValue().get("display_name").equals(display_name)){
                return entry.getValue();
            }
        }
        return null;
    }

    public static void equipHat(Player player){
        String equipped = Database.getEquipped(player);
        if(equipped == null) return;
        ItemStack cosmetic = cosmetics.get(equipped);
        if(cosmetic == null) player.sendMessage("❓ " + ChatColor.RED + "That cosmetic does not exist.");
        player.getInventory().setHelmet(cosmetic);
    }

    public static void giveCosmetic(Player player, String name){
        ItemStack cosmetic = cosmetics.get(name);
        if(cosmetic == null) player.sendMessage("❓ " + ChatColor.RED + "That cosmetic does not exist.");
        player.getInventory().addItem(cosmetic);
    }
}
