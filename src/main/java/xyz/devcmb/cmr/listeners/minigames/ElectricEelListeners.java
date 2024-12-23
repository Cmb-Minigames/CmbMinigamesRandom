package xyz.devcmb.cmr.listeners.minigames;

import net.kyori.adventure.text.Component;
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
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.List;
import java.util.Map;

/**
 * A class for listeners that are specific to the Electric Eel minigame
 */
public class ElectricEelListeners implements Listener {
    private static Location redStorageFromLocation;
    private static Location redStorageToLocation;
    private static Location blueStorageFromLocation;
    private static Location blueStorageToLocation;
    private static Location electricEelLocation;
    private static Location blueSpawnLocation;

    private final List<Material> breakableBlocks = List.of(
            Material.NETHER_QUARTZ_ORE,
            Material.RED_CONCRETE,
            Material.BLUE_CONCRETE
    );

    public static void NullLocations() {
        redStorageFromLocation = null;
        redStorageToLocation = null;
        blueStorageFromLocation = null;
        blueStorageToLocation = null;
        electricEelLocation = null;
        blueSpawnLocation = null;
    }

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

        electricEelLocation = Utilities.getLocationFromConfig(mapData, world, "electricEelSpawn");
        blueSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "blueSpawn");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        if(!breakableBlocks.contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        InitializeLocations();

        Player player = event.getPlayer();

        if(player.getInventory().getItemInOffHand().getType() == Material.NETHER_QUARTZ_ORE && event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            player.sendMessage(Component.text("You can't carry more than one uranium at a time!").color(Colors.RED));
            event.setCancelled(true);
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            ItemStack uranium = new ItemStack(Material.NETHER_QUARTZ_ORE, 1);
            ItemMeta meta = uranium.getItemMeta();
            assert meta != null;
            meta.displayName(Component.text("Uranium").color(Colors.GREEN));

            NamespacedKey key = new NamespacedKey(CmbMinigamesRandom.getPlugin(), "team");

            if (Utilities.isWithin(event.getBlock().getLocation(), redStorageFromLocation, redStorageToLocation)) {
                if (electricEelController.RED.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                electricEelController.redUranium--;
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "RED");
            } else if (Utilities.isWithin(event.getBlock().getLocation(), blueStorageFromLocation, blueStorageToLocation)) {
                if (electricEelController.BLUE.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                electricEelController.blueUranium--;
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "BLUE");
            }

            uranium.setItemMeta(meta);
            event.getPlayer().getInventory().setItemInOffHand(uranium);

            player.setGlowing(true);
            world.playSound(event.getBlock().getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);

            electricEelController.ResetBeams(electricEelLocation);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        InitializeLocations();

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            if (Utilities.isWithin(event.getBlock().getLocation(), redStorageFromLocation, redStorageToLocation)) {
                electricEelController.redUranium++;
            } else if (Utilities.isWithin(event.getBlock().getLocation(), blueStorageFromLocation, blueStorageToLocation)) {
                electricEelController.blueUranium++;
            }

            event.getPlayer().setGlowing(false);
            world.playSound(event.getBlock().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

            electricEelController.ResetBeams(electricEelLocation);
        }
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

            if (!electricEelController.hasStarted) {
                if (!Utilities.isWithin(player.getLocation(), blueStorageFromLocation, blueStorageToLocation)) {
                    event.getPlayer().teleport(blueSpawnLocation);
                }
            }
        }
    }
}
