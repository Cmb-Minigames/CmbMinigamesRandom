package xyz.devcmb.cmr.utils;

import org.bukkit.Material;

import java.util.Map;

/**
 * A utility class for storing custom model data constants
 */
public class CustomModelDataConstants {
    public static final Map<Material, Map<String, Number>> constants = Map.of(
        Material.ECHO_SHARD, Map.of(
            "blue_flag", 1,
            "red_flag", 2,
            "rocket_launcher", 3,
            "star_shower", 4,
            "cosmetic_inventory", 5,
            "empty_slot", 6
        )
    );
}
