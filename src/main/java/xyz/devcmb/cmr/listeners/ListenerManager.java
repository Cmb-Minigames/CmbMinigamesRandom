package xyz.devcmb.cmr.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.listeners.items.FireballListener;
import xyz.devcmb.cmr.listeners.items.MeteorShowerListener;
import xyz.devcmb.cmr.listeners.minigames.BrawlListeners;
import xyz.devcmb.cmr.listeners.minigames.CTFListeners;
import xyz.devcmb.cmr.listeners.minigames.KaboomersListeners;
import xyz.devcmb.cmr.listeners.minigames.SnifferCaretakerListeners;

public class ListenerManager {
    public static void initialize(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListeners(), plugin);
        pluginManager.registerEvents(new PregameLobbyProtections(), plugin);
        pluginManager.registerEvents(new DeathEffects(), plugin);
        pluginManager.registerEvents(new GeneralListeners(), plugin);

        // Minigame Listeners
        pluginManager.registerEvents(new MinigameListeners(), plugin);
        pluginManager.registerEvents(new CTFListeners(), plugin);
        pluginManager.registerEvents(new KaboomersListeners(), plugin);
        pluginManager.registerEvents(new BrawlListeners(), plugin);
        pluginManager.registerEvents(new SnifferCaretakerListeners(), plugin);

        // Items
        pluginManager.registerEvents(new MeteorShowerListener(), plugin);
        pluginManager.registerEvents(new FireballListener(), plugin);
    }
}
