package xyz.devcmb.cmr.commands.game;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.timers.TimerManager;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for pausing or unpausing the loop
 */
public class StopLoopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        commandSender.sendMessage(Component.text("Toggling loop...").color(Colors.GREEN));
        TimerManager.paused = !TimerManager.paused;
        return true;
    }
}
