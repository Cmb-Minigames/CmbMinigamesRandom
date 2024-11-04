package xyz.devcmb.cmr.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.minigames.Minigame;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MapLoader {
    public static String LOADED_MAP = null;
    public static void loadMap(String worldName){
        if (worldName == null) {
            CmbMinigamesRandom.LOGGER.warning("World name is null.");
            return;
        }

        File minigameWorld = new File(Bukkit.getWorldContainer(), worldName);
        if (!minigameWorld.exists()) {
            CmbMinigamesRandom.LOGGER.warning("World not found: " + worldName);
            return;
        }

        if (LOADED_MAP != null) {
            Bukkit.unloadWorld(LOADED_MAP, false);
        }

        WorldCreator wc = new WorldCreator(worldName);
        World world = CmbMinigamesRandom.getPlugin().getServer().createWorld(wc);
        world.setAutoSave(false);
        LOADED_MAP = worldName;

        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(Bukkit.getWorld(worldName).getSpawnLocation()));
    }

    public static void unloadMap(){
        if(LOADED_MAP != null){
            World world = Bukkit.getWorld(LOADED_MAP);
            if (world != null) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getType() == EntityType.ITEM_DISPLAY || entity.getType() == EntityType.BLOCK_DISPLAY) {
                        entity.remove();
                    }
                }
            }

            Bukkit.unloadWorld(LOADED_MAP, false);
        } else {
            CmbMinigamesRandom.LOGGER.warning("No map loaded to unload.");
        }
    }

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
