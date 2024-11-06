package xyz.devcmb.cmr.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.listeners.minigames.CTFListeners;

public class ListenerManager {
    public static void initialize(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListeners(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new PregameLobbyProtections(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new DeathEffects(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new MinigameListeners(), CmbMinigamesRandom.getPlugin());
        pluginManager.registerEvents(new CTFListeners(), CmbMinigamesRandom.getPlugin());
    }
}
