package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CookingChaosController;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class CookingChaosListeners implements Listener {
    private final List<Material> whitelist = List.of(
        Material.WHEAT,
        Material.PUMPKIN,
        Material.MELON,
        Material.MELON_SLICE,
        Material.WHEAT_SEEDS,
        Material.MELON_SEEDS,
        Material.PUMPKIN_SEEDS,
        Material.SUGAR_CANE,
        Material.CARROTS,
        Material.CARROT
    );

    private final List<Material> breakWhitelist = List.of(
        Material.ATTACHED_PUMPKIN_STEM,
        Material.ATTACHED_MELON_STEM
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

        if ((!whitelist.contains(block.getType())) || (!block.getType().equals(Material.FARMLAND) || !blockBelow.getType().equals(Material.FARMLAND))) {
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

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
        if (controller == null || GameManager.currentMinigame != controller) return;

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        Map<String, Object> redEntrance = (Map<String, Object>) mapData.get("redEntrance");
        Map<String, Object> blueEntrance = (Map<String, Object>) mapData.get("blueEntrance");

        Location redEntranceLocation = new Location(
            Bukkit.getWorld((String) mapData.get("worldName")),
            ((Integer) redEntrance.get("x")).doubleValue(),
            ((Integer) redEntrance.get("y")).doubleValue(),
            ((Integer) redEntrance.get("z")).doubleValue()
        );

        Location blueEntranceLocation = new Location(
            Bukkit.getWorld((String) mapData.get("worldName")),
            ((Integer) blueEntrance.get("x")).doubleValue(),
            ((Integer) blueEntrance.get("y")).doubleValue(),
            ((Integer) blueEntrance.get("z")).doubleValue()
        );

        if(controller.RED.contains(player)){
            for (Map<String, ?> customer : controller.redCustomers) {
                if (customer.get("entity").equals(entity)) {
                    Material order = (Material) customer.get("order");
                    if(player.getInventory().contains(order)){
                        player.getInventory().removeItem(new ItemStack(order, 1));
                        player.sendMessage(ChatColor.GREEN + "You have successfully served the customer!");
                        Integer tableIndex = (Integer) customer.get("tableIndex");
                        List<Entity> customers = (List<Entity>) controller.redTables.get(tableIndex).get("customers");

                        ((Entity) customer.get("orderTextEntity")).remove();

                        Utilities.moveEntity(entity, redEntranceLocation, 5 * 20);
                        customers.remove(entity);
                        if(customers.isEmpty()){
                            controller.redTables.get(tableIndex).put("taken", false);
                        }

                        controller.redCustomers.remove(customer);
                        controller.redScore += 1;

                        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), entity::remove, 5 * 20);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have the required item to serve the customer!");
                    }

                    break;
                }
            }
        } else if(controller.BLUE.contains(player)) {
            for (Map<String, ?> customer : controller.blueCustomers) {
                if (customer.get("entity").equals(entity)) {
                    Material order = (Material) customer.get("order");
                    if(player.getInventory().contains(order)){
                        player.getInventory().removeItem(new ItemStack(order, 1));
                        player.sendMessage(ChatColor.GREEN + "You have successfully served the customer!");
                        Integer tableIndex = (Integer) customer.get("tableIndex");
                        List<Entity> customers = (List<Entity>) controller.blueTables.get(tableIndex).get("customers");

                        ((Entity) customer.get("orderTextEntity")).remove();

                        Utilities.moveEntity(entity, blueEntranceLocation, 5 * 20);
                        customers.remove(entity);

                        if(customers.isEmpty()){
                            controller.blueTables.get(tableIndex).put("taken", false);
                        }

                        controller.blueCustomers.remove(customer);
                        controller.blueScore += 1;

                        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), entity::remove, 5 * 20);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have the required item to serve the customer!");
                    }

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event){
        CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
        if (controller == null || GameManager.currentMinigame != controller) return;
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            if(controller.RED.contains(player) && controller.RED.contains(damager)){
                event.setCancelled(true);
            } else if(controller.BLUE.contains(player) && controller.BLUE.contains(damager)){
                event.setCancelled(true);
            }
        }
    }
}
