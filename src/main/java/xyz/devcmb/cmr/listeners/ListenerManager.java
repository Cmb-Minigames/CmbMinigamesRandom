package xyz.devcmb.cmr.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;

public class ListenerManager {
    public static void initialize(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoin(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new PregameLobbyProtections(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new DeathEffects(), CmbMinigamesRandom.getPlugin());
    }
}
