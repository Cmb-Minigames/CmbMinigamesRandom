package xyz.devcmb.cmr.commands.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;

/**
 * A command for setting the stars of a player
 */
public class SetStarsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 2){
            commandSender.sendMessage("❓ " + ChatColor.RED + "Usage: /setstars <player> <stars>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("❓ " + ChatColor.RED + "Player not found.");
            return true;
        }

        try {
            int stars = Integer.parseInt(args[1]);
            Database.setUserStars(player, stars);
            commandSender.sendMessage(ChatColor.GREEN + "Set the stars of " + ChatColor.WHITE + Format.formatPlayerName(player) + ChatColor.GREEN + " to " + ChatColor.WHITE + stars + ChatColor.GREEN + ".");
        } catch (NumberFormatException e) {
            commandSender.sendMessage("❓ " + ChatColor.RED + "The stars value must be a number.");
            return true;
        }

        return true;
    }
}