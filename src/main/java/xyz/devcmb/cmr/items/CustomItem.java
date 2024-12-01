package xyz.devcmb.cmr.items;

import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    /**
     * @return The name of the custom item
     */
    String getName();

    /**
     * @return The item stack of the custom item
     */
    ItemStack getItemStack();
}
