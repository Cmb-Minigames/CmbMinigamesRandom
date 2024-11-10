package xyz.devcmb.cmr;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmr.commands.RegisterCommands;
import xyz.devcmb.cmr.listeners.ListenerManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.PacketManager;

import java.util.logging.Logger;

public final class CmbMinigamesRandom extends JavaPlugin {
    private static CmbMinigamesRandom plugin;
    public static Logger LOGGER;
    public static final boolean DeveloperMode = true;
    public static PacketManager packetManager = null;

    public static CmbMinigamesRandom getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = getLogger();

        saveDefaultConfig();
        LOGGER.info("Cmb Minigames has awoken. Initializing minigames...");

        packetManager = new PacketManager(this);
        ListenerManager.initialize();
        RegisterCommands.register();
        GameManager.registerAllMinigames();
        CMScoreboardManager.registerAllScoreboards();
        Database.connect();
    }

    @Override
    public void onDisable() {
        packetManager.cleanup();
        MapLoader.unloadMap();
        if(GameManager.ingame){
            GameManager.currentMinigame.stop();
        }
        Database.disconnect();
    }
}
