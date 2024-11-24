package xyz.devcmb.cmr;

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

public final class CmbMinigamesRandom extends JavaPlugin {
    private static CmbMinigamesRandom plugin;
    public static Logger LOGGER;
    public static final boolean DeveloperMode = true;
    private static BukkitAudiences adventure;

    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    public static CmbMinigamesRandom getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        saveDefaultConfig();
        LOGGER.info("Cmb Minigames has awoken. Initializing minigames...");

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
        if(GameManager.ingame){
            GameManager.currentMinigame.stop();
        }
        Database.disconnect();
    }
}
