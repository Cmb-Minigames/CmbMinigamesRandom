package xyz.devcmb.cmr.utils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.cosmetics.CosmeticInventory;

import java.util.*;

public class Utilities {
    private static final Map<Character, String> lowNumbersMap = new HashMap<>();

    static {
        lowNumbersMap.put('0', "\uE00C");
        lowNumbersMap.put('1', "\uE003");
        lowNumbersMap.put('2', "\uE004");
        lowNumbersMap.put('3', "\uE005");
        lowNumbersMap.put('4', "\uE006");
        lowNumbersMap.put('5', "\uE007");
        lowNumbersMap.put('6', "\uE008");
        lowNumbersMap.put('7', "\uE009");
        lowNumbersMap.put('8', "\uE00A");
        lowNumbersMap.put('9', "\uE00B");
    }

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

    public static <T> T getRandom(List<T> list){
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static String replaceNumbersWithLowOnes(String stringWithNumbers) {
        StringBuilder result = new StringBuilder();
        for (char c : stringWithNumbers.toCharArray()) {
            if (lowNumbersMap.containsKey(c)) {
                result.append(lowNumbersMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

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

    public static String formatTime(int time){
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

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

    public static void endGameResuable() {
        if(GameManager.intermisionRunnable != null) GameManager.intermisionRunnable.cancel();
        GameManager.intermisionRunnable = null;

        MapLoader.unloadMap();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().respawn();
            player.teleport(Objects.requireNonNull(Bukkit.getWorld("pregame")).getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
            player.setGlowing(false);
            GameManager.teamColors.put(player, ChatColor.WHITE);
            CosmeticInventory.giveInventoryItem(player);
        });

        GameManager.prepare();
    }

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
}
