package xyz.devcmb.cmr.commands;

import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.commands.completions.MinigameCompletion;

import java.util.Objects;

public class RegisterCommands {
    public static void register(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        Objects.requireNonNull(plugin.getCommand("pauseloop")).setExecutor(new StopLoopCommand());
        Objects.requireNonNull(plugin.getCommand("end")).setExecutor(new EndMinigameCommand());
        Objects.requireNonNull(plugin.getCommand("minigame")).setExecutor(new MinigameCommand(CmbMinigamesRandom.adventure()));
        Objects.requireNonNull(plugin.getCommand("flags")).setExecutor(new FlagsCommand(CmbMinigamesRandom.adventure()));

        // Completions
        Objects.requireNonNull(plugin.getCommand("minigame")).setTabCompleter(new MinigameCompletion());
        Objects.requireNonNull(plugin.getCommand("flags")).setTabCompleter(new MinigameCompletion());
    }
}
