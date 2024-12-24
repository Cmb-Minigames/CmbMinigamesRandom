package xyz.devcmb.cmr.commands.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for giving a cosmetic item to a player
 */
public class CosmeticCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 2){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /cosmeticitem <player> <name>").color(Colors.RED)));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Player not found").color(Colors.RED)));
            return true;
        }

        CosmeticManager.giveCosmetic(player, args[1]);

        return true;
    }
}
