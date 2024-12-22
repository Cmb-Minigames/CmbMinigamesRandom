package xyz.devcmb.cmr.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.timers.TimerManager;

/**
 * A command for ending the current minigame
 */
public class EndMinigameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + "Ending active timers...");
        TimerManager.endActiveTimers();
        return true;
    }
}
