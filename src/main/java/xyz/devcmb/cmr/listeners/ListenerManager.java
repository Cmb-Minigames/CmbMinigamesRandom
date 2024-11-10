package xyz.devcmb.cmr.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.listeners.minigames.CTFListeners;
import xyz.devcmb.cmr.listeners.minigames.KaboomersListeners;

public class ListenerManager {
    public static void initialize(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListeners(), plugin);
        pluginManager.registerEvents(new PregameLobbyProtections(), plugin);
        pluginManager.registerEvents(new DeathEffects(), plugin);
        pluginManager.registerEvents(new MinigameListeners(), plugin);
        pluginManager.registerEvents(new CTFListeners(), plugin);
        pluginManager.registerEvents(new GeneralListeners(), plugin);
        pluginManager.registerEvents(new KaboomersListeners(), plugin);
    }
}
