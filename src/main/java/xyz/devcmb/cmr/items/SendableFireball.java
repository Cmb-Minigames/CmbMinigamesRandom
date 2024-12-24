package xyz.devcmb.cmr.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.utils.Colors;

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
        meta.displayName(Component.text("Fireball").color(Colors.GOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Shoots a fireball in the direction you are looking")));
        item.setItemMeta(meta);
        return item;
    }
}
