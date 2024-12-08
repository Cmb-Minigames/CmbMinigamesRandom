package xyz.devcmb.cmr.minigames.teleporters;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.minigames.TeleportersController;

/**
 * The Funny Stick event
 */
public class FunnyStick implements TeleportersEvent {
    private final ItemStack funnyStick;
    private final TeleportersController teleportersController;

    public FunnyStick(TeleportersController controller){
        teleportersController = controller;

        funnyStick = new ItemStack(Material.STICK);
        ItemMeta meta = funnyStick.getItemMeta();
        if(meta == null) return;
        meta.setItemName("Funny Stick");
        funnyStick.setItemMeta(meta);
        funnyStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
    }

    @Override
    public void run() {
        teleportersController.players.forEach(player -> player.getInventory().addItem(funnyStick));

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            teleportersController.players.forEach(player -> player.getInventory().remove(funnyStick));

            teleportersController.eventTimer = 30;
            teleportersController.eventActive = false;
        }, 30 * 20);
    }

    @Override
    public String getName() {
        return "Funny Stick";
    }

    @Override
    public String getDescription() {
        return "Receive a knockback 5 stick for 30 seconds.";
    }
}
