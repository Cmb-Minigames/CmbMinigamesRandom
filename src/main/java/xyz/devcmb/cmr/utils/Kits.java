package xyz.devcmb.cmr.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class Kits {
    public static final Map<?, List<?>> ctf_kit = Map.of(
        Material.STONE_SWORD, List.of(1, 0), // Stone sword of quantity 1 on inventory slot 0
        Material.IRON_PICKAXE, List.of(1, 1), // Iron pickaxe of quantity 1 on inventory slot 1
        Material.GOLDEN_APPLE, List.of(2, 2), // 2 golden apples on inventory slot 2
        Material.ARROW, List.of(2, 6), // 64 arrows on inventory slot 3
        Material.BOW, List.of(1, 7), // Bow of quantity 1 on inventory slot 4
        KitEnums.COLORED_CONCRETE, List.of(64, 8), // 64 colored concrete on inventory slot 8
        Material.LEATHER_HELMET, List.of(1, KitEnums.HELMET), // Leather helmet of quantity 1 on helmet slot
        Material.LEATHER_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE), // Leather chestplate of quantity 1 on chestplate slot
        Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS), // Leather leggings of quantity 1 on leggings slot
        Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS) // Leather boots of quantity 1 on boots slot
    );

    public static void kitPlayer(Map<?, List<?>> kit, Player player, Material coloredConcrete){
        kit.forEach((key, value) -> {
            if(key == KitEnums.COLORED_CONCRETE){
                player.getInventory().setItem((int)value.get(1), new ItemStack(coloredConcrete, (int)value.get(0)));
            } else {
                if (value.get(1) == KitEnums.HELMET) {
                    player.getInventory().setHelmet(new ItemStack((Material) key));
                } else if (value.get(1) == KitEnums.CHESTPLATE) {
                    player.getInventory().setChestplate(new ItemStack((Material) key));
                } else if (value.get(1) == KitEnums.LEGGINGS) {
                    player.getInventory().setLeggings(new ItemStack((Material) key));
                } else if (value.get(1) == KitEnums.BOOTS) {
                    player.getInventory().setBoots(new ItemStack((Material) key));
                } else {
                    player.getInventory().setItem((int)value.get(1), new ItemStack((Material) key, (int)value.get(0)));
                }
            }
        });
    }
}
