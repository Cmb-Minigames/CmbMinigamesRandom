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

public class MapLoader {
    public static String LOADED_MAP = null;
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

//        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(world.getSpawnLocation()));
    }

    public static void unloadMap() {
        if (LOADED_MAP != null) {
            World world = Bukkit.getWorld(LOADED_MAP);
            if (world != null) {
                Bukkit.unloadWorld(LOADED_MAP, false);
            }

            File worldFolder = new File(Bukkit.getWorldContainer(), LOADED_MAP);
            try {
                FileUtils.deleteDirectory(worldFolder);
            } catch (IOException e) {
                CmbMinigamesRandom.LOGGER.warning("Failed to delete world folder: " + e.getMessage());
            }

            LOADED_MAP = null;
        } else {
            CmbMinigamesRandom.LOGGER.warning("No map loaded to unload.");
        }
    }

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
}
