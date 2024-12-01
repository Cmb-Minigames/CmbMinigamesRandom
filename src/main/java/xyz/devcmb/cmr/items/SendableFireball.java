package xyz.devcmb.cmr.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * A class for the sendable fireball item
 */
public class SendableFireball implements CustomItem {
    @Override
    public String getName() {
        return "Fireball";
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;
        meta.setItemName(ChatColor.GOLD + "Fireball");
        meta.setLore(List.of(ChatColor.WHITE + "Shoots a fireball in the direction you are looking"));
        item.setItemMeta(meta);
        return item;
    }
}
