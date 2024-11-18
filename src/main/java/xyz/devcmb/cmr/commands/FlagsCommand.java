package xyz.devcmb.cmr.commands;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;

import java.util.stream.Collectors;

public class FlagsCommand implements CommandExecutor {
    private final BukkitAudiences audiences;

    public FlagsCommand(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null) {
                String flags = minigame.getFlags().stream()
                        .map(Enum::toString)
                        .collect(Collectors.joining("\n"));

                audiences.sender(commandSender).sendMessage(Component.text()
                        .append(Component.text("-------------------------------------\n").color(NamedTextColor.AQUA))
                        .append(Component.text(minigame.getName() + "\n\n").color(NamedTextColor.WHITE))
                        .append(Component.text(flags)).color(NamedTextColor.GOLD)
                        .append(Component.text("\n-------------------------------------").color(NamedTextColor.AQUA))
                        .build());
            } else {
                commandSender.sendMessage("❓ " + ChatColor.RED + "Invalid minigame.");
            }
        } else {
            commandSender.sendMessage("❓ " + ChatColor.RED + "Invalid arguments. Usage: /flags <game>");
        }

        return true;
    }
}
