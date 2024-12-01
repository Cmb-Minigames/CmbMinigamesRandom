package xyz.devcmb.cmr.commands.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CrateManager;

/**
 * A command for rolling a crate for debug purposes
 */
public class RollCrateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 2){
            commandSender.sendMessage("❓" + ChatColor.RED + " Usage: /rollcrate <player> <name>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("❓" + ChatColor.RED + " Player not found.");
            return true;
        }

        String rolled = CrateManager.rollCrate(player, args[1]);
        if(rolled == null) return true;

        commandSender.sendMessage(ChatColor.GREEN + "Rolled a " + ChatColor.WHITE + rolled + ChatColor.GREEN + " from the crate.");

        return true;
    }
}
