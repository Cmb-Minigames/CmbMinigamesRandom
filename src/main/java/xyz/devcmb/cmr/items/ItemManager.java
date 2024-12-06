package xyz.devcmb.cmr.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for registering custom items
 */
public class ItemManager {
    public static Map<String, ItemStack> items = new HashMap<>();

    /**
     * Registers all custom items
     */
    public static void registerAllItems() {
        registerItem(new MeteorShower());
        registerItem(new SendableFireball());
    }

    /**
     * Registers a custom item
     * @param item The custom item to register
     */
    private static void registerItem(CustomItem item) {
        items.put(item.getName(), item.getItemStack());
    }
}
