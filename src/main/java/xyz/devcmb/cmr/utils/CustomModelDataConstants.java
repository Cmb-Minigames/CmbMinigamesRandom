package xyz.devcmb.cmr.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import java.util.Map;

/**
 * A utility class for storing custom model data constants
 */
public class CustomModelDataConstants {
    public static final Map<Material, Map<String, NamespacedKey>> constants = Map.of(
        Material.ECHO_SHARD, Map.of(
            "blue_flag", new NamespacedKey("cmbminigames", "functional/echo_shard/blue_flag"),
            "red_flag", new NamespacedKey("cmbminigames", "functional/echo_shard/red_flag"),
            "rocket_launcher", new NamespacedKey("cmbminigames", "functional/echo_shard/rocket_launcher"),
            "star_shower", new NamespacedKey("cmbminigames", "functional/echo_shard/star_shower"),
            "cosmetic_inventory", new NamespacedKey("cmbminigames", "functional/echo_shard/cosmetic_inventory"),
            "empty_slot", new NamespacedKey("cmbminigames", "functional/echo_shard/empty_slot")
        )
    );
}
