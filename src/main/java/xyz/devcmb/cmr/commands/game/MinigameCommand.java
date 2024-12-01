package xyz.devcmb.cmr.commands.game;

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

/**
 * A command for viewing certain information about a minigame
 */
public class MinigameCommand implements CommandExecutor {
    private final BukkitAudiences audiences;

    public MinigameCommand(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1){
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null){
                // don't worry nibbles, i don't understand this enough either
                audiences.sender(commandSender).sendMessage(Component.text()
                        .append(Component.text("-------------------------------------\n").color(NamedTextColor.AQUA))
                        .append(Component.text(minigame.getName() + "\n").color(NamedTextColor.WHITE))
                        .append(Component.text(minigame.getDescription() + "\n\n").color(NamedTextColor.GRAY))
                        .append(Component.text("Plays this session: ").color(NamedTextColor.WHITE))
                        .append(Component.text(GameManager.minigamePlays.get(minigame).toString()).color(NamedTextColor.AQUA))
                        .append(Component.text("\nFlags: ").color(NamedTextColor.WHITE))
                        .append(Component.text(String.valueOf(minigame.getFlags().size())).color(NamedTextColor.AQUA))
                        .append(Component.text("\n-------------------------------------").color(NamedTextColor.AQUA))
                        .build());
            } else {
                commandSender.sendMessage("❓ " + ChatColor.RED + "Invalid minigame.");
            }
        } else {
            commandSender.sendMessage("❓ " + ChatColor.RED + "Invalid arguments. Usage: /minigame <game>");
        }

        return true;
    }
}
