package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.ElectricEelController;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.Map;

public class ElectricEelListeners implements Listener {
    private Location redStorageFromLocation;
    private Location redStorageToLocation;
    private Location blueStorageFromLocation;
    private Location blueStorageToLocation;

    @SuppressWarnings("unchecked")
    private void InitializeLocations() {
        if(redStorageFromLocation != null && redStorageToLocation != null && blueStorageFromLocation != null && blueStorageToLocation != null) return;

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        redStorageFromLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "from");
        redStorageToLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "to");

        blueStorageFromLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "from");
        blueStorageToLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "to");
    }

    private static boolean isWithin(Location loc, Location point1, Location point2) {
        double minX = Math.min(point1.getX(), point2.getX());
        double minY = Math.min(point1.getY(), point2.getY());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxX = Math.max(point1.getX(), point2.getX());
        double maxY = Math.max(point1.getY(), point2.getY());
        double maxZ = Math.max(point1.getZ(), point2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        InitializeLocations();

        Player player = event.getPlayer();

        if(player.getInventory().getItemInOffHand().getType() == Material.NETHER_QUARTZ_ORE) {
            player.sendMessage(ChatColor.RED + "You can't carry more than one uranium at a time!");
            event.setCancelled(true);
            return;
        }

        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {

            ItemStack uranium = new ItemStack(Material.NETHER_QUARTZ_ORE, 1);
            ItemMeta meta = uranium.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.GREEN + "Uranium");

            NamespacedKey key = new NamespacedKey(CmbMinigamesRandom.getPlugin(), "team");

            if (isWithin(event.getBlock().getLocation(), redStorageFromLocation, redStorageToLocation)) {
                if (electricEelController.RED.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                electricEelController.redUranium--;
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "RED");
            } else if (isWithin(event.getBlock().getLocation(), blueStorageFromLocation, blueStorageToLocation)) {
                if (electricEelController.BLUE.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                electricEelController.blueUranium--;
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "BLUE");
            }

            uranium.setItemMeta(meta);
            event.getPlayer().getInventory().setItemInOffHand(uranium);
        }

        electricEelController.ResetBeams();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        InitializeLocations();

        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            if (isWithin(event.getBlock().getLocation(), redStorageFromLocation, redStorageToLocation)) {
                electricEelController.redUranium++;
            } else if (isWithin(event.getBlock().getLocation(), blueStorageFromLocation, blueStorageToLocation)) {
                electricEelController.blueUranium++;
            }
        }

        electricEelController.ResetBeams();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        Player player = event.getPlayer();

        if(electricEelController.BLUE.contains(event.getPlayer())) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER) {
                PotionEffect dolphinsGrace = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 255, false, false);
                player.addPotionEffect(dolphinsGrace);

                PotionEffect waterBreathing = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255, false, false);
                player.addPotionEffect(waterBreathing);
            } else {
                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1, false, false);
                player.addPotionEffect(slowness);

                PotionEffect miningFatigue = new PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 0, false, false);
                player.addPotionEffect(miningFatigue);
            }
        }
    }
}
