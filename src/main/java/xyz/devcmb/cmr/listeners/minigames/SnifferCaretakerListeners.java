package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.*;
import org.bukkit.block.Furnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;
import xyz.devcmb.cmr.utils.MapLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SnifferCaretakerListeners implements Listener {
    private final List<Material> breakableBlocks = List.of(
        Material.DIRT,
        Material.GRASS_BLOCK,
        Material.HAY_BLOCK,
        Material.WHEAT,
        Material.COCOA,
        Material.ACACIA_LOG,
        Material.ACACIA_LEAVES,
        Material.COAL_ORE,
        Material.DEEPSLATE_COAL_ORE,
        Material.SHORT_GRASS,
        Material.TALL_GRASS,
        Material.POPPY,
        Material.DANDELION,
        Material.RED_CONCRETE,
        Material.BLUE_CONCRETE
    );

    private final Map<Material, Integer> snifferRequestedItems = Map.of(
        Material.DIRT, 2,
        Material.WHEAT, 5,
        Material.HAY_BLOCK, 50,
        Material.COOKIE, 25,
        Material.MUTTON, 10,
        Material.COOKED_MUTTON, 70
    );

    private Location redBaseFromLocation;
    private Location redBaseToLocation;
    private Location blueBaseFromLocation;
    private Location blueBaseToLocation;

    @SuppressWarnings("unchecked")
    private void InitializeLocations() {
        if (redBaseFromLocation != null && redBaseToLocation != null && blueBaseFromLocation != null && blueBaseToLocation != null) {
            return;
        }

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

        Map<String, Object> redBaseFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("redBasePreventPlace")).get("from");
        Map<String, Object> redBaseTo = (Map<String, Object>)((Map<String, Object>) mapData.get("redBasePreventPlace")).get("to");

        if (redBaseFrom == null || redBaseTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Red base points are not defined.");
            return;
        }

        redBaseFromLocation = new Location(
                world,
                ((Number) redBaseFrom.get("x")).doubleValue(),
                ((Number) redBaseFrom.get("y")).doubleValue(),
                ((Number) redBaseFrom.get("z")).doubleValue()
        );

        redBaseToLocation = new Location(
                world,
                ((Number) redBaseTo.get("x")).doubleValue(),
                ((Number) redBaseTo.get("y")).doubleValue(),
                ((Number) redBaseTo.get("z")).doubleValue()
        );

        Map<String, Object> blueBaseFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("blueBasePreventPlace")).get("from");
        Map<String, Object> blueBaseTo = (Map<String, Object>)((Map<String, Object>) mapData.get("blueBasePreventPlace")).get("to");

        if (blueBaseFrom == null || blueBaseTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Blue base points are not defined.");
            return;
        }

        blueBaseFromLocation = new Location(
                world,
                ((Number) blueBaseFrom.get("x")).doubleValue(),
                ((Number) blueBaseFrom.get("y")).doubleValue(),
                ((Number) blueBaseFrom.get("z")).doubleValue()
        );

        blueBaseToLocation = new Location(
                world,
                ((Number) blueBaseTo.get("x")).doubleValue(),
                ((Number) blueBaseTo.get("y")).doubleValue(),
                ((Number) blueBaseTo.get("z")).doubleValue()
        );
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
    public void onBlockBreak(BlockBreakEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        InitializeLocations();

        if (isWithin(event.getBlock().getLocation(), redBaseFromLocation, redBaseToLocation) || isWithin(event.getBlock().getLocation(), blueBaseFromLocation, blueBaseToLocation)) {
            event.setCancelled(true);
        }

        if (!breakableBlocks.contains(event.getBlock().getType())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        InitializeLocations();

        if (isWithin(event.getBlock().getLocation(), redBaseFromLocation, redBaseToLocation) || isWithin(event.getBlock().getLocation(), blueBaseFromLocation, blueBaseToLocation)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFuelTakenOut(InventoryClickEvent event) {
        if (event.getInventory() instanceof FurnaceInventory furnaceInventory) {
            if (event.getSlot() == 1 && furnaceInventory.getFuel() != null) {
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (event.getBlock().getState() instanceof Furnace furnace) {
            FurnaceInventory inventory = furnace.getInventory();
            inventory.setFuel(new ItemStack(Material.COAL, 64));
            furnace.update();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        if (event.getEntity().getType() == EntityType.SHEEP) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.MUTTON, new Random().nextInt(1, 3)));
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        if (controller == null || GameManager.currentMinigame != controller) return;

        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        Material material = itemDrop.getItemStack().getType();

        if (controller.RED.contains(player) && itemDrop.getLocation().distance(controller.redSniffer.getLocation()) > 7.0) return;
        if (controller.BLUE.contains(player) && itemDrop.getLocation().distance(controller.blueSniffer.getLocation()) > 7.0) return;

        if (snifferRequestedItems.containsKey(material)) {
            int happinessIncrease = snifferRequestedItems.get(material) * itemDrop.getItemStack().getAmount();
            if (controller.RED.contains(player)) {
                controller.redSnifferHappiness = Math.clamp(controller.redSnifferHappiness + happinessIncrease, 0, 1000);
                player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_EAT, 10, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_HAPPY, 10, 1);
                player.sendMessage(ChatColor.RED + "[Red Sniffer] " + ChatColor.RESET + (happinessIncrease >= 50 ? "This makes me VERY happy!" : "This makes me happy!"));
            }
            if (controller.BLUE.contains(player)) {
                controller.blueSnifferHappiness = Math.clamp(controller.blueSnifferHappiness + happinessIncrease, 0, 1000);
                player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_EAT, 10, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_HAPPY, 10, 1);
                player.sendMessage(ChatColor.BLUE + "[Blue Sniffer] " + ChatColor.RESET + (happinessIncrease >= 50 ? "This makes me VERY happy!" : "This makes me happy!"));
            }
            itemDrop.remove();
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_SNIFFER_SNIFFING, 10, 1);
            if (controller.RED.contains(player)) player.sendMessage(ChatColor.RED + "[Red Sniffer] I don't want that!");
            if (controller.BLUE.contains(player)) player.sendMessage(ChatColor.BLUE + "[Blue Sniffer] " + ChatColor.RED + "I don't want that!");
        }
    }
}
