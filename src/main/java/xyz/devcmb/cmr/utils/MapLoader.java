package xyz.devcmb.cmr.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.minigames.Minigame;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A utility class for loading and unloading maps
 */
public class MapLoader {
    public static String LOADED_MAP = null;

    /**
     * Clones a map
     * @param worldName The name of the world to load
     */
    public static void loadMap(String worldName) {
        MultiverseCore multiverseCore = CmbMinigamesRandom.getMultiverseCore();
        if(multiverseCore == null){
            CmbMinigamesRandom.LOGGER.warning("Multiverse-Core not found.");
            return;
        }

        if(LOADED_MAP != null){
            unloadMap();
        }

        MVWorldManager worldManager = multiverseCore.getMVWorldManager();
        String uniqueWorldName = "minigame-" + UUID.randomUUID();

        boolean success = worldManager.cloneWorld(worldName, uniqueWorldName);
        if(!success){
            CmbMinigamesRandom.LOGGER.warning("Failed to clone world: " + worldName);
            return;
        }

        LOADED_MAP = uniqueWorldName;
    }

    /**
     * Unload the currently loaded map
     */
    public static void unloadMap() {
        MultiverseCore multiverseCore = CmbMinigamesRandom.getMultiverseCore();
        if(multiverseCore == null){
            CmbMinigamesRandom.LOGGER.warning("Multiverse-Core not found.");
            return;
        }

        if (LOADED_MAP != null) {
            World world = Bukkit.getWorld(LOADED_MAP);
            if (world == null){
                CmbMinigamesRandom.LOGGER.warning("World not found: " + LOADED_MAP);
                return;
            }

            MVWorldManager worldManager = multiverseCore.getMVWorldManager();
            if(worldManager.isMVWorld(LOADED_MAP)){
                worldManager.deleteWorld(LOADED_MAP);
                LOADED_MAP = null;
            } else {
                CmbMinigamesRandom.LOGGER.warning("World not managed by Multiverse: " + LOADED_MAP);
            }
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
        MultiverseCore multiverseCore = CmbMinigamesRandom.getMultiverseCore();
        if(multiverseCore == null){
            CmbMinigamesRandom.LOGGER.warning("Multiverse-Core not found.");
            return;
        }

        MVWorldManager worldManager = multiverseCore.getMVWorldManager();

        File rootDirectory = Bukkit.getWorldContainer();
        File[] files = rootDirectory.listFiles((dir, name) -> name.startsWith("minigame-") && new File(dir, name).isDirectory());

        if (files != null) {
            for (File file : files) {
                if(worldManager.isMVWorld(file.getName())){
                    worldManager.deleteWorld(file.getName());
                    CmbMinigamesRandom.LOGGER.info("Deleted minigame world: " + file.getName());
                }
            }
        }
    }
}