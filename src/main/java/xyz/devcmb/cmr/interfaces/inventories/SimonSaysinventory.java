package xyz.devcmb.cmr.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SimonSaysinventory implements HandledInventory {
    @Override
    public Inventory getInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Simon Says");

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            inventory.setItem(45 + i, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
            inventory.setItem(i * 9 + 8, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        // TODO

        return inventory;
    }
}
