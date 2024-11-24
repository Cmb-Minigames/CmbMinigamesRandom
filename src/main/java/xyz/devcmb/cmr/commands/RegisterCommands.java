package xyz.devcmb.cmr.commands;

import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.commands.completions.CosmeticCommandCompletion;
import xyz.devcmb.cmr.commands.completions.CrateCommandCompletion;
import xyz.devcmb.cmr.commands.completions.MinigameCompletion;
import xyz.devcmb.cmr.commands.completions.UICompletions;

import java.util.Objects;

public class RegisterCommands {
    public static void register(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        Objects.requireNonNull(plugin.getCommand("pauseloop")).setExecutor(new StopLoopCommand());
        Objects.requireNonNull(plugin.getCommand("end")).setExecutor(new EndMinigameCommand());
        Objects.requireNonNull(plugin.getCommand("minigame")).setExecutor(new MinigameCommand(CmbMinigamesRandom.adventure()));
        Objects.requireNonNull(plugin.getCommand("flags")).setExecutor(new FlagsCommand(CmbMinigamesRandom.adventure()));
        Objects.requireNonNull(plugin.getCommand("select")).setExecutor(new SelectCommand());
        Objects.requireNonNull(plugin.getCommand("ui")).setExecutor(new UICommand());
        Objects.requireNonNull(plugin.getCommand("cosmeticitem")).setExecutor(new CosmeticCommand());
        Objects.requireNonNull(plugin.getCommand("crateitem")).setExecutor(new CrateCommand());
        Objects.requireNonNull(plugin.getCommand("rollcrate")).setExecutor(new RollCrateCommand());

        // Completions
        Objects.requireNonNull(plugin.getCommand("minigame")).setTabCompleter(new MinigameCompletion());
        Objects.requireNonNull(plugin.getCommand("flags")).setTabCompleter(new MinigameCompletion());
        Objects.requireNonNull(plugin.getCommand("select")).setTabCompleter(new MinigameCompletion());
        Objects.requireNonNull(plugin.getCommand("ui")).setTabCompleter(new UICompletions());
        Objects.requireNonNull(plugin.getCommand("cosmeticitem")).setTabCompleter(new CosmeticCommandCompletion());
        Objects.requireNonNull(plugin.getCommand("crateitem")).setTabCompleter(new CrateCommandCompletion());
        Objects.requireNonNull(plugin.getCommand("rollcrate")).setTabCompleter(new CrateCommandCompletion());
    }
}
