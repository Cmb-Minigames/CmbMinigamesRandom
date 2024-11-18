package xyz.devcmb.cmr.interfaces.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface HandledInventory {
    Inventory getInventory(Player player);
}
