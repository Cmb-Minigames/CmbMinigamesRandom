package xyz.devcmb.cmr.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CrateManager;

public class CrateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 2){
            commandSender.sendMessage("❓" + ChatColor.RED + " Usage: /crateitem <player> <name>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("❓" + ChatColor.RED + " Player not found.");
            return true;
        }

        CrateManager.giveCrate(player, args[1]);

        return true;
    }
}
