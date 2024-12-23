package xyz.devcmb.cmr.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;

import java.util.List;

/**
 * A class for the meteor shower item
 */
public class MeteorShower implements CustomItem {

    @Override
    public String getName() {
        return "Meteor Shower";
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;
        meta.setItemModel(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("star_shower"));
        meta.displayName(Component.text("Meteor Shower").color(Colors.RED).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Summons 5 fireballs 40 blocks above you in a circle pattern").color(Colors.WHITE)));
        item.setItemMeta(meta);
        return item;
    }
}
