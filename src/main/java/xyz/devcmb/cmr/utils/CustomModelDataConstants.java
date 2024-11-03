package xyz.devcmb.cmr.utils;

import org.bukkit.Material;

import java.util.Map;

public class CustomModelDataConstants {
    public static final Map<Material, Map<String, Number>> constants = Map.of(
        Material.ECHO_SHARD, Map.of(
            "blue_flag", 1,
            "red_flag", 2
        )
    );
}
