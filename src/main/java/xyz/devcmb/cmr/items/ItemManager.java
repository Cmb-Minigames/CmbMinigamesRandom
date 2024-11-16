package xyz.devcmb.cmr.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
    public static Map<String, ItemStack> items = new HashMap<>();
    public static void registerAllItems() {
        registerItem(new MeteorShower());
        registerItem(new SendableFireball());
    }

    private static void registerItem(CustomItem item) {
        items.put(item.getName(), item.getItemStack());
    }
}
