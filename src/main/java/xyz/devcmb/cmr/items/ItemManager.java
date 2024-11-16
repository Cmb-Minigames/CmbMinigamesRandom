package xyz.devcmb.cmr.items;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemManager {
    public static Map<String, ItemStack> items;
    public static void registerAllItems() {

    }

    private static void registerItem(String name, ItemStack item) {
        items.put(name, item);
    }
}
