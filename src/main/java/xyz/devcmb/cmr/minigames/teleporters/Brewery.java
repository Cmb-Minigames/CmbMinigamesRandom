package xyz.devcmb.cmr.minigames.teleporters;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import xyz.devcmb.cmr.minigames.TeleportersController;

public class Brewery implements TeleportersEvent {
    public TeleportersController teleportersController;
    private final ItemStack jumpPotion;
    private final ItemStack speedPotion;

    public Brewery(TeleportersController controller){
        teleportersController = controller;

        jumpPotion = new ItemStack(Material.POTION);
        speedPotion = new ItemStack(Material.POTION);

        PotionMeta jumpMeta = (PotionMeta) jumpPotion.getItemMeta();
        PotionMeta speedMeta = (PotionMeta) speedPotion.getItemMeta();

        if(jumpMeta == null || speedMeta == null) return;

        jumpMeta.setItemName("Splash Potion of Jump Boost");
        speedMeta.setItemName("Splash Potion of Speed");

        jumpMeta.setBasePotionType(PotionType.LEAPING);
        speedMeta.setBasePotionType(PotionType.SWIFTNESS);

        jumpPotion.setItemMeta(jumpMeta);
        speedPotion.setItemMeta(speedMeta);
    }

    @Override
    public void run() {
        teleportersController.players.forEach(player -> {
            player.getInventory().addItem(jumpPotion);
            player.getInventory().addItem(speedPotion);
        });

        teleportersController.eventTimer = 30;
        teleportersController.eventActive = false;
    }

    @Override
    public String getName() {
        return "Brewery";
    }

    @Override
    public String getDescription() {
        return "Receive a jump boost and speed boost potion.";
    }
}
