package xyz.devcmb.cmr.utils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.cosmetics.CosmeticInventory;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;

import java.util.*;

/**
 * A utility class for methods reused across the entire plugin
 */
public class Utilities {
    /**
     * A countdown title sequence to display to a player
     * @param player The player to display the countdown title to
     * @param totalSeconds The total seconds to countdown
     */
    public static void Countdown(Player player, int totalSeconds){
        new BukkitRunnable(){
            int seconds = totalSeconds;
            @Override
            public void run() {
                if(seconds == 0){
                    this.cancel();
                    player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", "", 0, 40, 10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 2.5f);
                    return;
                }

                ChatColor color = ChatColor.WHITE;

                switch(seconds){
                    case 3:
                        color = ChatColor.GREEN;
                        break;
                    case 2:
                        color = ChatColor.YELLOW;
                        break;
                    case 1:
                        color = ChatColor.RED;
                        break;
                    default:
                        break;
                }

                player.sendTitle(color.toString() + ChatColor.BOLD + "> " + seconds + " <", "The game will begin shortly", 0, 20, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 1);
                seconds--;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    /**
     * Get a random element from a list
     * @param list The list to get a random element from
     * @param <T> The type of the list
     * @return A random element from the list
     */
    public static <T> T getRandom(List<T> list){
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    /**
     * Find a valid location to spawn a player at
     * @param spawnLocation The location to check vaiation for
     * @return A valid location to spawn a player at
     */
    public static Location findValidLocation(Location spawnLocation) {
        Location newLocation = spawnLocation.clone();

        if (!Objects.requireNonNull(newLocation.getWorld()).getNearbyEntities(newLocation, 1, 1, 1).isEmpty()) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && zOffset == 0) continue;

                    Location checkLocation = newLocation.clone().add(xOffset, 0, zOffset);

                    if (Objects.requireNonNull(checkLocation.getWorld()).getNearbyEntities(checkLocation, 1, 1, 1).isEmpty()) {
                        return checkLocation;
                    }
                }
            }
        }

        return newLocation;
    }

    /**
     * Get all blocks in a radius around a location
     * @param center The center location
     * @param radius The radius to get blocks from
     * @return A list of blocks in the radius
     */
    public static List<Block> getBlocksInRadius(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();
        if(world == null) return List.of();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    /**
     * Format a time in seconds to a string
     * @param time The time in seconds
     * @return The formatted time string
     */
    public static String formatTime(int time){
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Fill a chest with random items
     * @param chestData The chest to fill
     * @param items The items to fill the chest with
     * @param min The minimum amount of items to fill the chest with
     * @param max The maximum amount of items to fill the chest with
     * @return The filled chest
     */
    public static Chest fillChestRandomly(Chest chestData, List<ItemStack> items, Integer min, Integer max) {
        Inventory chestInventory = chestData.getBlockInventory();
        chestInventory.clear();

        Random random = new Random();
        int amount = random.nextInt(max - min + 1) + min;

        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            ItemStack item = getRandom(items);
            int slot;

            do {
                slot = random.nextInt(chestInventory.getSize());
            } while (slots.contains(slot));

            chestInventory.setItem(slot, item);
            slots.add(slot);
        }

        return chestData;
    }

    /**
     * Fill a range of blocks with a specific block
     * @param fromLocation The starting location
     * @param toLocation The ending location
     * @param fillBlock The block to fill with
     */
    public static void fillBlocks(Location fromLocation, Location toLocation, Material fillBlock){
        World world = fromLocation.getWorld();
        if (world == null || !world.equals(toLocation.getWorld())) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }

        int minX = Math.min(fromLocation.getBlockX(), toLocation.getBlockX());
        int maxX = Math.max(fromLocation.getBlockX(), toLocation.getBlockX());
        int minY = Math.min(fromLocation.getBlockY(), toLocation.getBlockY());
        int maxY = Math.max(fromLocation.getBlockY(), toLocation.getBlockY());
        int minZ = Math.min(fromLocation.getBlockZ(), toLocation.getBlockZ());
        int maxZ = Math.max(fromLocation.getBlockZ(), toLocation.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(fillBlock);
                }
            }
        }
    }

    /**
     * A reusable method to reset certain things for the game
     */
    public static void gameStartReusable(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.removePotionEffect(PotionEffectType.HUNGER);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            player.setFoodLevel(20);
        });
    }

    /**
     * A reusable method to reset certain things for the end of the game
     */
    public static void endGameResuable() {
        if(GameManager.intermisionRunnable != null) GameManager.intermisionRunnable.cancel();
        GameManager.intermisionRunnable = null;

        MapLoader.unloadMap(false);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().respawn();
            player.teleport(Objects.requireNonNull(Bukkit.getWorld("pregame")).getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
            player.setGlowing(false);
            GameManager.teamColors.put(player, ChatColor.WHITE);

            CosmeticInventory cosmeticInventory = CosmeticManager.playerInventories.get(player);
            cosmeticInventory.giveInventoryItem();

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            PotionEffect hungerEffect = new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 255, true, false, false);
            player.addPotionEffect(hungerEffect);
        });

        GameManager.prepare();
    }

    /**
     * Move an entity to a location over a duration
     * @param entity The entity to move
     * @param newLocation The location to move the entity to
     * @param duration The duration to move the entity over
     */
    public static void moveEntity(Entity entity, Location newLocation, int duration) {
        new BukkitRunnable() {
            private final Location startLocation = entity.getLocation();
            private final double deltaX = (newLocation.getX() - startLocation.getX()) / duration;
            private final double deltaY = (newLocation.getY() - startLocation.getY()) / duration;
            private final double deltaZ = (newLocation.getZ() - startLocation.getZ()) / duration;
            private int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed >= duration) {
                    entity.teleport(newLocation);
                    this.cancel();
                    return;
                }

                Location currentLocation = entity.getLocation();

                currentLocation.setYaw(newLocation.getYaw());
                currentLocation.setPitch(newLocation.getPitch());

                currentLocation.add(deltaX, deltaY, deltaZ);
                entity.teleport(currentLocation);

                ticksElapsed++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 1);
    }

    /**
     * Set a player to be invisible to all other players
     * @param player The player to set invisible
     */
    public static void setInvisible(Player player){
//        player.setInvisible(true);
        Bukkit.getOnlinePlayers().forEach(plr -> {
            if(plr != player){
                plr.hidePlayer(CmbMinigamesRandom.getPlugin(), player);
            }
        });
    }

    /**
     * Set a player to be visible to all other players
     * @param player The player to set invisible
     */
    public static void setVisible(Player player){
//        player.setInvisible(false);
        Bukkit.getOnlinePlayers().forEach(plr -> {
            if(plr != player){
                plr.showPlayer(CmbMinigamesRandom.getPlugin(), player);
            }
        });
    }

    public static List<Player> respawningPlayers = new ArrayList<>();

    @SuppressWarnings("all")
    /**
     * A custom respawn method for players to prevent the music from stopping upon death
     * @param player The player to respawn
     * @param damageSource The damage source that killed the player
     */
    public static void customRespawn(Player player, DamageSource damageSource){
        if(respawningPlayers.contains(player)) return;
        respawningPlayers.add(player);

        List<ItemStack> inventoryContents = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        inventoryContents.addAll(Arrays.asList(player.getInventory().getArmorContents()));
        PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, damageSource, inventoryContents, 0, null);
        Bukkit.getPluginManager().callEvent(deathEvent);
        
        player.setAllowFlight(true);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        player.setFoodLevel(20);
        player.setFlying(true);
        player.setInvulnerable(true);
        setInvisible(player);
        player.getInventory().clear();

        new BukkitRunnable(){
            Integer loops = 3;
            @Override
            public void run() {
                if(loops == 0){
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.setInvulnerable(false);
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setFireTicks(0);
                    player.setExp(0);
                    player.setLevel(0);
                    setVisible(player);
                    player.sendTitle(ChatColor.AQUA + "RESPAWNED", "", 5, 40, 5);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

                    PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, new Location(player.getWorld(), 0, 100, 0), false);
                    Bukkit.getPluginManager().callEvent(respawnEvent);

                    respawningPlayers.remove(player);
                    this.cancel();
                    return;
                }
                player.sendTitle(ChatColor.AQUA + loops.toString(), "", 5, 10, 5);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
                loops--;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    /**
     * Get a location from a map data configuration
     * @param mapData The map data configuration
     * @param world The world to get the location in
     * @param path The path to get the location from, can be nested like "path.subpath"
     * @return The location from the map data
     */
    @SuppressWarnings("unchecked")
    public static Location getLocationFromConfig(Map<String, Object> mapData, World world, String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = mapData;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                CmbMinigamesRandom.LOGGER.warning(keys[i] + " is not a map in the path " + path);
                return null;
            }
        }

        Map<String, Object> locationData = (Map<String, Object>) currentMap.get(keys[keys.length - 1]);

        if (locationData == null) {
            CmbMinigamesRandom.LOGGER.warning(path + " is not defined in the map data.");
            return null;
        }

        if (locationData.get("x") == null || locationData.get("y") == null || locationData.get("z") == null) {
            CmbMinigamesRandom.LOGGER.warning(path + " is not properly configured in the map data.");
            return null;
        }

        Location loc = new Location(
                world,
                ((Number) locationData.get("x")).doubleValue(),
                ((Number) locationData.get("y")).doubleValue(),
                ((Number) locationData.get("z")).doubleValue()
        );

        if (locationData.get("yaw") != null) {
            loc.setYaw(((Number) locationData.get("yaw")).floatValue());
        }

        if (locationData.get("pitch") != null) {
            loc.setPitch(((Number) locationData.get("pitch")).floatValue());
        }

        return loc;
    }

    /**
     * Get a location from a map data configuration
     * @param mapData The map data configuration
     * @param world The world to get the location in
     * @param key The key to get the location from
     * @param subkey The subkey to get the location from
     * @return The location from the map data
     */
    @SuppressWarnings("unchecked")
    public static Location getLocationFromConfig(Map<String, Object> mapData, World world, String key, String subkey) {
        Map<String, Object> parentData = (Map<String, Object>) mapData.get(key);

        if (parentData == null) {
            CmbMinigamesRandom.LOGGER.warning(key + " is not defined in the map data.");
            return null;
        }

        Map<String, Object> locationData = (Map<String, Object>) parentData.get(subkey);

        if (locationData == null) {
            CmbMinigamesRandom.LOGGER.warning(key + "/" + subkey + " is not defined in the map data.");
            return null;
        }

        if(locationData.get("x") == null || locationData.get("y") == null || locationData.get("z") == null){
            CmbMinigamesRandom.LOGGER.warning(key + "/" + subkey + " is not properly configured in the map data.");
            return null;
        }

        if(locationData.get("pitch") != null && locationData.get("yaw") != null){
            return new Location(
                    world,
                    ((Number) locationData.get("x")).doubleValue(),
                    ((Number) locationData.get("y")).doubleValue(),
                    ((Number) locationData.get("z")).doubleValue(),
                    ((Number) locationData.get("yaw")).floatValue(),
                    ((Number) locationData.get("pitch")).floatValue()
            );
        }

        return new Location(
                world,
                ((Number) locationData.get("x")).doubleValue(),
                ((Number) locationData.get("y")).doubleValue(),
                ((Number) locationData.get("z")).doubleValue()
        );
    }

    public static boolean isWithin(Location loc, Location point1, Location point2) {
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
}
