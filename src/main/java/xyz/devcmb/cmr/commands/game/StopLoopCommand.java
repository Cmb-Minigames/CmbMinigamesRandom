package xyz.devcmb.cmr.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.timers.TimerManager;

/**
 * A command for pausing or unpausing the loop
 */
public class StopLoopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + "Toggling loop...");
        TimerManager.paused = !TimerManager.paused;
        return true;
    }
}
