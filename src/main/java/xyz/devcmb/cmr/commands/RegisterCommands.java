package xyz.devcmb.cmr.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.commands.completions.*;
import xyz.devcmb.cmr.commands.cosmetics.*;
import xyz.devcmb.cmr.commands.development.*;
import xyz.devcmb.cmr.commands.game.*;

import java.util.Objects;

/**
 * A class for registering commands and tab completions
 */
public class RegisterCommands {
    /**
     * Registers all commands and tab completions
     */
    public static void register(){
        registerSingleCommand("pause", new StopLoopCommand());
        registerSingleCommand("end", new EndMinigameCommand());
        registerSingleCommand("minigame", new MinigameCommand());
        registerSingleCommand("flags", new FlagsCommand());
        registerSingleCommand("select", new SelectCommand());
        registerSingleCommand("ui", new UICommand());
        registerSingleCommand("cosmeticitem", new CosmeticCommand());
        registerSingleCommand("crateitem", new CrateCommand());
        registerSingleCommand("rollcrate", new RollCrateCommand());
        registerSingleCommand("rc", new ReloadCosmeticsCommand());
        registerSingleCommand("cosmetic", new GiveCosmeticCommand());
        registerSingleCommand("crate", new GiveCrateCommand());
        registerSingleCommand("setstars", new SetStarsCommand());
        registerSingleCommand("actionbar", new ToggleActionBarCommand());
        registerSingleCommand("fade", new FadeCommand());

        // Completions
        registerSingleTabCompletion("minigame", new MinigameCompletion());
        registerSingleTabCompletion("flags", new MinigameCompletion());
        registerSingleTabCompletion("select", new MinigameCompletion());
        registerSingleTabCompletion("ui", new UICompletions());
        registerSingleTabCompletion("cosmeticitem", new CosmeticCommandCompletion());
        registerSingleTabCompletion("cosmetic", new CosmeticCommandCompletion());
        registerSingleTabCompletion("crateitem", new CrateCommandCompletion());
        registerSingleTabCompletion("rollcrate", new CrateCommandCompletion());
        registerSingleTabCompletion("crate", new CrateCommandCompletion());

    }

    /**
     * Registers a single command
     * @param command The command name
     * @param executor The command executor class
     */
    public static void registerSingleCommand(String command, CommandExecutor executor){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(executor);
    }

    /**
     * Registers a single tab completion
     * @param command The command name
     * @param completer The tab completer class
     */
    public static void registerSingleTabCompletion(String command, TabCompleter completer){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        Objects.requireNonNull(plugin.getCommand(command)).setTabCompleter(completer);
    }
}
