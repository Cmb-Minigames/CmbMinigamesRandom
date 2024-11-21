package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CookingChaosController;

import java.util.List;
import java.util.Random;

public class CookingChaosListeners implements Listener {
    private final List<Material> whitelist = List.of(
        Material.WHEAT,
        Material.PUMPKIN,
        Material.PUMPKIN_STEM,
        Material.MELON,
        Material.MELON_SLICE,
        Material.WHEAT_SEEDS,
        Material.MELON_SEEDS,
        Material.PUMPKIN_SEEDS,
        Material.OAK_SAPLING,
        Material.SWEET_BERRIES
    );

    private final List<Material> breakWhitelist = List.of(
        Material.OAK_LOG,
        Material.OAK_LEAVES
    );

    private final List<Material> ore_list = List.of(
        Material.IRON_ORE,
        Material.GOLD_ORE,
        Material.REDSTONE_ORE,
        Material.DIAMOND_ORE,
        Material.COAL_ORE,
        Material.EMERALD_ORE
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
        if (controller == null || GameManager.currentMinigame != controller) return;

        Player player = event.getPlayer();

        Block block = event.getBlock();
        if(!whitelist.contains(block.getType()) && !ore_list.contains(block.getType()) && !breakWhitelist.contains(block.getType())){
            event.setCancelled(true);
            return;
        }

        if(ore_list.contains(block.getType())){
            event.setCancelled(true);
            event.setDropItems(false);
            block.getDrops().forEach(item -> player.getInventory().addItem(item));

            block.setType(Material.BEDROCK);
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> block.setType(ore_list.get(new Random().nextInt(ore_list.size()))), 20 * 20);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
        if (controller == null || GameManager.currentMinigame != controller) return;

        Block block = event.getBlock();
        Block blockBelow = block.getLocation().add(0, -1, 0).getBlock();

        if (!whitelist.contains(block.getType()) && blockBelow.getType() != Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer == null) return;
        if (event.getEntityType() == EntityType.CHICKEN) {
            if (new Random().nextInt(4) == 0) {
                event.getDrops().add(new ItemStack(Material.EGG));
                killer.sendMessage(ChatColor.GOLD + "The chicken seems to have dropped something...");
            }
        }
    }
}
