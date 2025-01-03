package xyz.devcmb.cmr.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;

import java.util.List;
import java.util.Map;

/**
 * A utility class for kits
 */
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

    public static final Map<?, List<?>> brawl_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(Material.COOKED_BEEF, List.of(8, 2)), // 8 cooked beef on inventory slot 2
            Map.entry(Material.GOLDEN_APPLE, List.of(2, 3)), // 2 golden apples on inventory slot 3
            Map.entry(KitEnums.COLORED_CONCRETE, List.of(64, 8)), // 64 colored concrete on inventory slot 8
            Map.entry(Material.LEATHER_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE)), // Leather chestplate of quantity 1 on chestplate slot
            Map.entry(Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS)), // Leather leggings of quantity 1 on leggings slot
            Map.entry(Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS)) // Leather boots of quantity 1 on boots slot
    );

    public static final Map<?, List<?>> sniffercaretaker_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(Material.STONE_SHOVEL, List.of(1, 2)), // Stone shovel of quantity 1 on inventory slot 2
            Map.entry(Material.STONE_HOE, List.of(1, 3)), // Stone hoe of quantity 1 on inventory slot 3
            Map.entry(Material.BONE_MEAL, List.of(10, 4)), // Bone meal of quantity 10 on inventory slot 4
            Map.entry(Material.WHEAT_SEEDS, List.of(64, 5)), // Seeds of quantity 64 on inventory slot 5
            Map.entry(KitEnums.COLORED_CONCRETE, List.of(64, 8)), // 64 colored concrete on inventory slot 8
            Map.entry(Material.LEATHER_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE)), // Leather chestplate of quantity 1 on chestplate slot
            Map.entry(Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS)), // Leather leggings of quantity 1 on leggings slot
            Map.entry(Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS)) // Leather boots of quantity 1 on boots slot
    );

    public static final Map<?, List<?>> cookingchaos_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(Material.COOKED_BEEF, List.of(8, 2)), // 8 cooked beef on inventory slot 2
            Map.entry(Material.BOW, List.of(1, 6)), // Bow of quantity 1 on inventory slot 6
            Map.entry(Material.ARROW, List.of(12, 7)), // 12 arrows on inventory slot 7
            Map.entry(Material.OAK_PLANKS, List.of(64, 8)) // 64 oak planks on inventory slot 8
    );

    public static final Map<?, List<?>> electriceel_kit = Map.ofEntries(
            Map.entry(Material.STONE_SWORD, List.of(1, 0)), // Stone sword of quantity 1 on inventory slot 0
            Map.entry(Material.IRON_PICKAXE, List.of(1, 1)), // Iron pickaxe of quantity 1 on inventory slot 1
            Map.entry(Material.COOKED_BEEF, List.of(8, 2)), // 8 cooked beef on inventory slot 2
            Map.entry(KitEnums.COLORED_CONCRETE, List.of(64, 8)), // 64 colored concrete on inventory slot 8
            Map.entry(Material.LEATHER_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE)), // Leather chestplate of quantity 1 on chestplate slot
            Map.entry(Material.LEATHER_LEGGINGS, List.of(1, KitEnums.LEGGINGS)), // Leather leggings of quantity 1 on leggings slot
            Map.entry(Material.LEATHER_BOOTS, List.of(1, KitEnums.BOOTS)) // Leather boots of quantity 1 on boots slot
    );

    public static final Map<?, List<?>> teleporters_kit = Map.ofEntries(
        Map.entry(Material.ENDER_PEARL, List.of(16, 0)), // 16 ender pearls on inventory slot 0
        Map.entry(Material.COOKED_BEEF, List.of(2, 1)), // 2 cooked beef on inventory slot 1
        Map.entry(Material.IRON_CHESTPLATE, List.of(1, KitEnums.CHESTPLATE)) // Iron chestplate of quantity 1 on chestplate slot
    );

    /**
     * Give a player a kit from the lists above
     * @param kit The kit to give
     * @param player The player to give the kit to
     * @param coloredConcrete The colored concrete to give
     */
  
    public static void kitPlayer(Map<?, List<?>> kit, Player player, Material coloredConcrete){
        kit.forEach((key, value) -> {
            if(key == KitEnums.COLORED_CONCRETE) {
                player.getInventory().setItem((int) value.get(1), new ItemStack(coloredConcrete, (int) value.get(0)));
            } else if(key == KitEnums.ROCKET_LAUNCHER){
                ItemStack rocketLauncher = new ItemStack(Material.ECHO_SHARD, (int) value.getFirst());
                ItemMeta meta = rocketLauncher.getItemMeta();
                if(meta == null) return;
                meta.displayName(Component.text("Rocket Launcher").decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("rocket_launcher"));
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

        CosmeticManager.equipHat(player);
    }
}
