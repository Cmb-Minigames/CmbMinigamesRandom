package xyz.devcmb.cmr;

import com.onarandombox.MultiverseCore.MultiverseCore;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.devcmb.cmr.commands.RegisterCommands;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.items.ItemManager;
import xyz.devcmb.cmr.listeners.ListenerManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.MusicBox;

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
    private static BukkitAudiences adventure;
    private static MultiverseCore multiverseCore;

    /**
     * Get the Adventure instance
     * @return The Adventure instance
     */
    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

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

        adventure = BukkitAudiences.create(this);
        Database.connect();
        CosmeticManager.registerAllCosmetics();
        CrateManager.registerAllCrates();
        MusicBox.registerAllTracks();
        ListenerManager.initialize();
        RegisterCommands.register();
        ItemManager.registerAllItems();
        GameManager.registerAllMinigames();
        CMScoreboardManager.registerAllScoreboards();
    }

    @Override
    public void onDisable() {
        MapLoader.unloadMap();
        Database.disconnect();
        MapLoader.cleanup();
    }
}
