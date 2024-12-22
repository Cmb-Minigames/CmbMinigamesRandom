package xyz.devcmb.cmr.commands.development;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.interfaces.Fade;

public class FadeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 3){
            commandSender.sendMessage("❓ " + ChatColor.RED + "Usage: /fade <up> <stay> <down>");
            return false;
        }

        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage("❓ " + ChatColor.RED + "Only players can use this command.");
            return false;
        }

        try {
            Integer.parseInt(args[0]);
            Integer.parseInt(args[1]);
            Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage("❓ " + ChatColor.RED + "Invalid numbers.");
            return false;
        }

        Fade.fadePlayer(player, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        return true;
    }
}
