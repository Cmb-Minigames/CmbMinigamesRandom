package xyz.devcmb.cmr.utils;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

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

    public static void Countdown(Player player, int totalSeconds) {
        new BukkitRunnable() {
            int seconds = totalSeconds;

            @Override
            public void run() {
                if (seconds == 0) {
                    this.cancel();
                    player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", "", 0, 40, 10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 2.5f);
                    return;
                }

                ChatColor color = ChatColor.WHITE;

                switch (seconds) {
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

    public static <T> T getRandom(List<T> list) {
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
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        if (xOffset == 0 && yOffset == 0 && zOffset == 0) continue;

                        Location checkLocation = newLocation.clone().add(xOffset, yOffset, zOffset);

                        if (Objects.requireNonNull(checkLocation.getWorld()).getNearbyEntities(checkLocation, 1, 1, 1).isEmpty()) {
                            return checkLocation;
                        }
                    }
                }
            }
        }

        return newLocation;
    }

    public static List<Block> getBlocksInRadius(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();
        if (world == null) return List.of();
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

    public static String formatTime(int time) {
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

    public static void showAdvancement(Player player, String item, String title, String description) {
        NamespacedKey advancementKey = new NamespacedKey(CmbMinigamesRandom.getPlugin(), "customadvancement_" + title.toLowerCase().replace(" ", "_"));
        String json = String.format("""
            {
                "criteria": {
                    "requirement": {
                        "trigger": "minecraft:location",
                        "conditions": {}
                    }
                },
                "requirements": [["requirement"]],
                "display": {
                    "title": {"text": "%s"},
                    "description": {"text": "%s"},
                    "icon": {
                        "item": "%s"
                    },
                    "frame": "task",
                    "show_toast": true,
                    "announce_to_chat": false,
                    "hidden": false,
                    "background": "minecraft:textures/gui/advancements/backgrounds/stone.png"
                }
            }""",
        title, description, item);


        try {
            Bukkit.getUnsafe().loadAdvancement(advancementKey, json);

            Advancement advancement = Bukkit.getAdvancement(advancementKey);
            if (advancement != null) {
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                if (!progress.isDone()) {
                    progress.getRemainingCriteria().forEach(progress::awardCriteria);
                }
            }

            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                Bukkit.getUnsafe().removeAdvancement(advancementKey);
            }, 20L);
        } catch (Exception e) {
            CmbMinigamesRandom.LOGGER.warning("Failed to show advancement: " + e.getMessage());
        }
    }
}
