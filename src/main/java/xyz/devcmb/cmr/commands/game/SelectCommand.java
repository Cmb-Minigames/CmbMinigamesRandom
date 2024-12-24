package xyz.devcmb.cmr.commands.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for selecting the next minigame
 */
public class SelectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 1){
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null){
                GameManager.selectedMinigame = minigame;
                Bukkit.broadcast(Component.text("✅ ")
                        .append(Component.text(minigame.getName()).decorate(TextDecoration.BOLD))
                        .append(Component.text(" has been selected as the next minigame by an administrator."))
                        .color(Colors.GREEN)
                );
            } else {
                sender.sendMessage(Component.text("❓ ").append(Component.text("Invalid Minigame").color(Colors.RED)));
            }
        } else {
            sender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /select <minigame>").color(Colors.RED)));
        }
        return true;
    }
}
