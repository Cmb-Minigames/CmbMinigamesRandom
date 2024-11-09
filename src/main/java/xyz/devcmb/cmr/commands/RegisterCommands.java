package xyz.devcmb.cmr.commands;

import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.Objects;

public class RegisterCommands {
    public static void register(){
        CmbMinigamesRandom plugin = CmbMinigamesRandom.getPlugin();
        Objects.requireNonNull(plugin.getCommand("pauseloop")).setExecutor(new StopLoopCommand());
    }
}
