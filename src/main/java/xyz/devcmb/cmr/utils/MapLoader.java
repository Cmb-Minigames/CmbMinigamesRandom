package xyz.devcmb.cmr.utils;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.minigames.Minigame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A utility class for loading and unloading maps
 */
public class MapLoader {
    public static String LOADED_MAP = null;

    /**
     * Clone a map from it's name and teleport all players to it
     * @param worldName The name of the world to clone
     */
    public static void loadMap(String worldName) {
        if (worldName == null) {
            CmbMinigamesRandom.LOGGER.warning("World name is null.");
            return;
        }

        File minigameWorld = new File(Bukkit.getWorldContainer(), worldName);
        if (!minigameWorld.exists()) {
            CmbMinigamesRandom.LOGGER.warning("World not found: " + worldName);
            return;
        }

        String uniqueWorldName = "minigame-" + UUID.randomUUID();
        File uniqueWorldFolder = new File(Bukkit.getWorldContainer(), uniqueWorldName);

        try {
            FileUtils.copyDirectory(minigameWorld, uniqueWorldFolder);
            File uidFile = new File(uniqueWorldFolder, "uid.dat");
            if (uidFile.exists()) {
                Files.delete(uidFile.toPath());
            }
        } catch (IOException e) {
            CmbMinigamesRandom.LOGGER.warning("Failed to clone world: " + e.getMessage());
            return;
        }

        if (LOADED_MAP != null) {
            Bukkit.unloadWorld(LOADED_MAP, false);
        }

        WorldCreator wc = new WorldCreator(uniqueWorldName);
        World world = CmbMinigamesRandom.getPlugin().getServer().createWorld(wc);
        if (world == null) return;
        world.setAutoSave(false);
        LOADED_MAP = uniqueWorldName;
    }

    /**
     * Unload the currently loaded map
     * @param closing If the server is closing, in which case, do not begin a runnable
     */
    public static void unloadMap(boolean closing) {
        if (LOADED_MAP != null) {
            World world = Bukkit.getWorld(LOADED_MAP);
            if (world == null){
                CmbMinigamesRandom.LOGGER.warning("World not found: " + LOADED_MAP);
                return;
            }

            world.setAutoSave(false);
            Bukkit.unloadWorld(LOADED_MAP, false);

            String oldWorldName = LOADED_MAP;
            LOADED_MAP = null;

            if(closing) return;
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                File worldFolder = new File(Bukkit.getWorldContainer(), oldWorldName);
                try {
                    FileUtils.deleteDirectory(worldFolder);
                } catch (IOException e) {
                    CmbMinigamesRandom.LOGGER.warning("Failed to delete world folder: " + e.getMessage());
                }
            }, 40 * 20);
        } else {
            CmbMinigamesRandom.LOGGER.warning("No map loaded to unload.");
        }
    }

    /**
     * Load a random map for a minigame
     * @param minigame The minigame to load a map for
     * @return The map data
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ?> loadRandomMap(Minigame minigame){
        List<Map<String, ?>> maps = (List<Map<String, ?>>) CmbMinigamesRandom.getPlugin().getConfig().getList("maps." + minigame.getName());
        if(maps == null || maps.isEmpty()){
            CmbMinigamesRandom.LOGGER.warning("No maps found for minigame: " + minigame.getName());
            return null;
        }

        Map<String, ?> map = maps.get((int) (Math.random() * maps.size()));
        CmbMinigamesRandom.LOGGER.info(map.toString());
        loadMap((String)((Map<String, ?>) map.get("map")).get("worldName"));
        return map;
    }

    /**
     * Delete all minigame worlds
     */
    public static void cleanup(){
        File rootDirectory = Bukkit.getWorldContainer();
        File[] files = rootDirectory.listFiles((dir, name) -> name.startsWith("minigame-") && new File(dir, name).isDirectory());

        if (files != null) {
            for (File file : files) {
                try {
                    FileUtils.deleteDirectory(file);
                    CmbMinigamesRandom.LOGGER.info("Deleted minigame world: " + file.getName());
                } catch (IOException e) {
                    CmbMinigamesRandom.LOGGER.warning("Failed to delete world: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
    }
}