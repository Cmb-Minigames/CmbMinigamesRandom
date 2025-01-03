package xyz.devcmb.cmr.commands.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.utils.Colors;

import java.util.stream.Collectors;

/**
 * A command for listing the flags of a minigame
 */
public class FlagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null) {
                String flags = minigame.getFlags().stream()
                        .map(Enum::toString)
                        .collect(Collectors.joining("\n"));

                commandSender.sendMessage(Component.text()
                        .append(Component.text("-------------------------------------\n").color(NamedTextColor.AQUA))
                        .append(Component.text(minigame.getName() + "\n\n").color(NamedTextColor.WHITE))
                        .append(Component.text(flags)).color(NamedTextColor.GOLD)
                        .append(Component.text("\n-------------------------------------").color(NamedTextColor.AQUA))
                );
            } else {
                commandSender.sendMessage(Component.text("❓ ").append(Component.text("Invalid minigame").color(Colors.RED)));
            }
        } else {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /flags <minigame>").color(Colors.RED)));
        }

        return true;
    }
}
