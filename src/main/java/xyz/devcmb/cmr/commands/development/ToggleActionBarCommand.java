package xyz.devcmb.cmr.commands.development;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.interfaces.ActionBar;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for toggling the action bar task
 */
public class ToggleActionBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Only players can use this command").color(Colors.RED)));
            return true;
        }

        if(ActionBar.sendActionBarTasks.containsKey(player)){
            ActionBar.unregisterPlayer(player);
            commandSender.sendMessage(Component.text("✅ Action bar task has been unregistered.").color(Colors.GREEN));
        } else {
            ActionBar.registerPlayer(player);
            commandSender.sendMessage(Component.text("✅ Action bar task has been registered.").color(Colors.GREEN));
        }

        return true;
    }
}
