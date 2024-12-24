package xyz.devcmb.cmr;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmr.commands.RegisterCommands;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.items.ItemManager;
import xyz.devcmb.cmr.listeners.ListenerManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.MusicBox;
import xyz.devcmb.cmr.timers.TimerManager;

import java.util.logging.Logger;

/**
 * The main class for the CmbMinigamesRandom plugin.
 * <br>
 * This class serves as the entry point for the plugin.
 */
public final class CmbMinigamesRandom extends JavaPlugin {
    private static CmbMinigamesRandom plugin;
    public static Logger LOGGER;
    public static boolean DeveloperMode = false;
    private static MultiverseCore multiverseCore;

    /**
     * Get the plugin instance
     * @return The plugin instance
     */
    public static CmbMinigamesRandom getPlugin() {
        return plugin;
    }

    /**
     * Get the MultiverseCore instance
     * @return The MultiverseCore instance
     */
    public static MultiverseCore getMultiverseCore() {
        return multiverseCore;
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        saveDefaultConfig();
        LOGGER.info("Cmb Minigames has awoken. Initializing minigames...");

        DeveloperMode = getPlugin().getConfig().getBoolean("settings.devMode");
        multiverseCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

        Database.connect();
        CosmeticManager.registerAllCosmetics();
        CrateManager.registerAllCrates();
        MusicBox.registerAllTracks();
        ListenerManager.initialize();
        RegisterCommands.register();
        ItemManager.registerAllItems();
        GameManager.registerAllMinigames();
        CMScoreboardManager.registerAllScoreboards();
        TimerManager.registerAllTimers();
    }

    @Override
    public void onDisable() {
        MapLoader.unloadMap();
        Database.disconnect();
        MapLoader.cleanup();
    }
}
