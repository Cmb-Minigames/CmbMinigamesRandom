package xyz.devcmb.cmr.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.devcmb.cmr.GameManager;

public class StopLoopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + "Toggling loop...");
        GameManager.paused = !GameManager.paused;
        return true;
    }
}
