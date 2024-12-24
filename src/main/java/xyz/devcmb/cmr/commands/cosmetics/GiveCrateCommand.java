package xyz.devcmb.cmr.commands.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;

import java.util.Objects;

/**
 * A command for giving a crate to a player
 */
public class GiveCrateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /crate <player> <crate>").color(Colors.RED)));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: Player not found").color(Colors.RED)));
            return true;
        }

        ItemStack crateStack = CrateManager.crates.get(args[1]);
        if(crateStack == null || crateStack.getItemMeta() == null){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Crate not found").color(Colors.RED)));
            return true;
        }

        Database.giveCrate(player, args[1]);

        Component message = Component.text("Gave ").color(Colors.GREEN)
                .append(Component.text(Format.formatPlayerName(player)).color(Colors.WHITE))
                .append(Component.text(" the ")).color(Colors.GREEN)
                .append(Objects.requireNonNull(crateStack.getItemMeta().displayName())).color(Colors.WHITE)
                .append(Component.text(" crate.").color(Colors.GREEN));

        commandSender.sendMessage(message);

        return true;
    }
}
