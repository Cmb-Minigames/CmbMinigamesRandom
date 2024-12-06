package xyz.devcmb.cmr.commands.development;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.interfaces.ActionBar;

public class ToggleActionBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("❓ " + ChatColor.RED + "You must be a player to use this command.");
            return true;
        }

        if(ActionBar.sendActionBarTasks.containsKey(player)){
            ActionBar.unregisterPlayer(player);
            commandSender.sendMessage(ChatColor.GREEN + "✅ Action bar task has been unregistered.");
        } else {
            ActionBar.registerPlayer(player);
            commandSender.sendMessage(ChatColor.GREEN + "✅ Action bar task has been registered.");
        }

        return true;
    }
}
