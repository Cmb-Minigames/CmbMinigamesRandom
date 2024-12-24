package xyz.devcmb.cmr.commands.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;

import java.util.Objects;

/**
 * A command for giving a cosmetic to a player
 */
public class GiveCosmeticCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /cosmetic <player> <cosmetic>").color(Colors.RED)));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Player not found.").color(Colors.RED)));

            return true;
        }

        ItemStack cosmeticStack = CosmeticManager.cosmetics.get(args[1]);
        if(cosmeticStack == null || cosmeticStack.getItemMeta() == null){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Cosmetic not found.").color(Colors.RED)));
            return true;
        }

        Database.giveCosmetic(player, args[1]);

        Component text = Component.text("Gave ").color(Colors.GREEN)
                .append(Component.text(Format.formatPlayerName(player)).color(Colors.WHITE))
                .append(Component.text(" the ")).color(Colors.GREEN)
                .append(Objects.requireNonNull(cosmeticStack.getItemMeta().displayName())).color(Colors.WHITE)
                .append(Component.text(" cosmetic.")).color(Colors.GREEN);

        commandSender.sendMessage(text);

        return true;
    }
}
