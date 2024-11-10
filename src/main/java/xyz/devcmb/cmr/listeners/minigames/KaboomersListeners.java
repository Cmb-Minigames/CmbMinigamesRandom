package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.KaboomersController;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.HashMap;
import java.util.UUID;

public class KaboomersListeners implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId) && (currentTime - cooldowns.get(playerId)) < 1000) {
            player.sendMessage(ChatColor.RED + "You must wait before using this item again!");
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ECHO_SHARD) {
            ItemMeta meta = item.getItemMeta();
            if (
                    meta != null
                    && meta.hasCustomModelData()
                    && meta.getCustomModelData() == (int)CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("rocket_launcher")
            ) {
                Fireball fireball = player.launchProjectile(Fireball.class, player.getLocation().getDirection());
                fireball.setYield(0);
                cooldowns.put(playerId, currentTime);
                player.setCooldown(Material.ECHO_SHARD, 20);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        KaboomersController controller = (KaboomersController)GameManager.getMinigameByName("Kaboomers");
        if(controller == null || GameManager.currentMinigame != controller) return;
        if (event.getEntity() instanceof Fireball fireball) {
            if (fireball.getShooter() instanceof Player shooter) {
                if((!controller.RED.contains(shooter) && !controller.BLUE.contains(shooter)) || event.getHitBlock() == null) return;
                event.setCancelled(true);
                Utilities.getBlocksInRadius(event.getHitBlock().getLocation(), 1).forEach(block -> {
                    if (block.getType() != Material.AIR) {
                        controller.redBlocks.remove(block);
                        controller.blueBlocks.remove(block);

                        if(controller.RED.contains(shooter)) {
                            controller.redBlocks.add(block);
                            block.setType(Material.RED_WOOL);
                        } else {
                            controller.blueBlocks.add(block);
                            block.setType(Material.BLUE_WOOL);
                        }
                    }
                });
            }
        }
    }
}