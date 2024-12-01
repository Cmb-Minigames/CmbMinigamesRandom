package xyz.devcmb.cmr.listeners.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;

import java.util.Objects;

/**
 * A class for meteor shower event listeners
 */
public class MeteorShowerListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("star_shower").intValue()) {
                    Location playerLocation = event.getPlayer().getLocation();
                    for (int i = 0; i < 5; i++) {
                        double angle = i * (4 * Math.PI / 5);
                        double x = playerLocation.getX() + 4 * Math.cos(angle);
                        double z = playerLocation.getZ() + 4 * Math.sin(angle);
                        Location fireballLocation = new Location(playerLocation.getWorld(), x, playerLocation.getY() + 40, z);
                        Fireball fireball = Objects.requireNonNull(playerLocation.getWorld()).spawn(fireballLocation, Fireball.class);
                        fireball.setDirection(new Vector(0, -1, 0));
                        fireball.setYield(2);
                    }
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }
}