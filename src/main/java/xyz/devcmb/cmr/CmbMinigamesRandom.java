package xyz.devcmb.cmr;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmr.listeners.ListenerManager;

import java.util.logging.Logger;

public final class CmbMinigamesRandom extends JavaPlugin {
    private static CmbMinigamesRandom plugin;
    public static Logger LOGGER;
    public static final boolean DeveloperMode = true;

    public static CmbMinigamesRandom getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        LOGGER.info("Cmb Minigames has awoken. Initializing minigames...");

        ListenerManager.initialize();
        GameManager.registerAllMinigames();
    }

    @Override
    public void onDisable() {

    }
}
