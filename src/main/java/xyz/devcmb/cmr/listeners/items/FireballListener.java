package xyz.devcmb.cmr.listeners.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Objects;

/**
 * A class for fireball event listeners
 */
public class FireballListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType().equals(Material.FIRE_CHARGE)) {
                Location playerLocation = event.getPlayer().getLocation();
                Location fireballLocation = playerLocation.clone().add(0,1,0).add(playerLocation.getDirection().multiply(2));
                Fireball fireball = Objects.requireNonNull(playerLocation.getWorld()).spawn(fireballLocation, Fireball.class);
                fireball.setDirection(playerLocation.getDirection());
                fireball.setYield(2);
                item.setAmount(item.getAmount() - 1);
            }
        }
    }
}
