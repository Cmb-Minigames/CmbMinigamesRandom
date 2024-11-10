package xyz.devcmb.cmr.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class Kits {
    public static final Map<?, List<?>> ctf_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(Material.GOLDEN_APPLE, List.of(2, 2)), // 2 golden apples on inventory slot 2
            Map.entry(Material.ARROW, List.of(2, 6)), // 64 arrows on inventory slot 3
            Map.entry(Material.BOW, List.of(1, 7)), // Bow of quantity 1 on inventory slot 4
            Map.entry(KitEnums.COLORED_CONCRETE, List.of(64, 8)), // 64 colored concrete on inventory slot 8
            Map.entry(Material.LEATHER_HELMET, List.of(1, KitEnums.HELMET)), // Leather helmet of quantity 1 on helmet slot
            Map.entry(Material.LEATHER_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE)), // Leather chestplate of quantity 1 on chestplate slot
            Map.entry(Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS)), // Leather leggings of quantity 1 on leggings slot
            Map.entry(Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS)) // Leather boots of quantity 1 on boots slot
    );

    public static final Map<?, List<?>> kaboomers_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(KitEnums.ROCKET_LAUNCHER, List.of(1, 2)), // Rocket launcher of quantity 1 on inventory slot 2
            Map.entry(Material.COOKED_BEEF, List.of(6, 3)), // 6 cooked beef on inventory slot 3
            Map.entry(Material.FIREWORK_ROCKET, List.of(1, 4)), // 1 firework in the 4th inventory slot
            Map.entry(Material.LEATHER_CHESTPLATE, List.of(1, 5)), // Leather chestplace to replace your elytra with
            Map.entry(Material.SHEARS, List.of(1, 6)), // Shears of quantity 1 on inventory slot 6
            Map.entry(KitEnums.COLORED_CONCRETE, List.of(64, 8)), // 64 colored concrete on inventory slot 8
            Map.entry(Material.LEATHER_HELMET, List.of(1, KitEnums.HELMET)), // Leather helmet of quantity 1 on helmet slot
            Map.entry(Material.ELYTRA, List.of(1, KitEnums.CHESTPLATE)), // Elytra of quantity 1 on chestplate slot
            Map.entry(Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS)), // Leather leggings of quantity 1 on leggings slot
            Map.entry(Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS)) // Leather boots of quantity 1 on boots slot
    );

    public static void kitPlayer(Map<?, List<?>> kit, Player player, Material coloredConcrete){
        kit.forEach((key, value) -> {
            if(key == KitEnums.COLORED_CONCRETE) {
                player.getInventory().setItem((int) value.get(1), new ItemStack(coloredConcrete, (int) value.get(0)));
            } else if(key == KitEnums.ROCKET_LAUNCHER){
                ItemStack rocketLauncher = new ItemStack(Material.ECHO_SHARD, (int) value.getFirst());
                ItemMeta meta = rocketLauncher.getItemMeta();
                meta.setItemName("Rocket Launcher");
                meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("rocket_launcher").intValue());
                rocketLauncher.setItemMeta(meta);

                player.getInventory().setItem((int) value.get(1), rocketLauncher);
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
