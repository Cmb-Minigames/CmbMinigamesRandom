package xyz.devcmb.cmr.commands.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;

/**
 * A command for setting the stars of a player
 */
public class SetStarsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 2){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /setstars <player> <stars>").color(Colors.RED)));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Player not found").color(Colors.RED)));
            return true;
        }

        try {
            int stars = Integer.parseInt(args[1]);
            Database.setUserStars(player, stars);

            Component message = Component.text("Set the stars of ").color(Colors.GREEN)
                    .append(Component.text(Format.formatPlayerName(player)).color(Colors.WHITE))
                    .append(Component.text(" to ")).color(Colors.GREEN)
                    .append(Component.text(String.valueOf(stars)).color(Colors.WHITE))
                    .append(Component.text(".")).color(Colors.GREEN);

            commandSender.sendMessage(message);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Stars must be a number.").color(Colors.RED)));
            return true;
        }

        return true;
    }
}