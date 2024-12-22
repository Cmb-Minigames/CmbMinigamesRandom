package xyz.devcmb.cmr.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        meta.setItemName(ChatColor.RED + "Meteor Shower");
        meta.setLore(List.of(ChatColor.WHITE + "Summons 5 fireballs 40 blocks above you in a circle pattern"));
        item.setItemMeta(meta);
        return item;
    }
}
