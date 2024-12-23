package xyz.devcmb.cmr.commands.development;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.utils.Colors;

public class FadeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 3){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /fade <up> <stay> <down>").color(Colors.RED)));
            return false;
        }

        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Only players can use this command").color(Colors.RED)));
            return false;
        }

        try {
            Integer.parseInt(args[0]);
            Integer.parseInt(args[1]);
            Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Invalid numbers").color(Colors.RED)));
            return false;
        }

        Fade.fadePlayer(player, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        return true;
    }
}
