package xyz.devcmb.cmr.minigames.teleporters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.minigames.TeleportersController;

public class Restock implements TeleportersEvent {
    public TeleportersController teleportersController;

    public Restock(TeleportersController controller){
        teleportersController = controller;
    }

    @Override
    public void run() {
        teleportersController.players.forEach(player -> player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16)));

        teleportersController.eventTimer = 60;
        teleportersController.eventActive = false;
    }

    @Override
    public String getName() {
        return "Restock";
    }

    @Override
    public String getDescription() {
        return "Get 16 more ender pearls.";
    }
}
