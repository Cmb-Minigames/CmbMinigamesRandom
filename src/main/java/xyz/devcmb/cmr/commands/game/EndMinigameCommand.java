package xyz.devcmb.cmr.commands.game;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.timers.TimerManager;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for ending the current minigame
 */
public class EndMinigameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage(Component.text("✅ Ending active timers...").color(Colors.GREEN));
        TimerManager.endActiveTimers();
        return true;
    }
}
