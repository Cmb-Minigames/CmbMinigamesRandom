package xyz.devcmb.cmr.commands.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for viewing certain information about a minigame
 */
public class MinigameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1){
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null){
                commandSender.sendMessage(Component.text()
                        .append(Component.text("-------------------------------------\n").color(NamedTextColor.AQUA))
                        .append(Component.text(minigame.getName() + "\n").color(NamedTextColor.WHITE))
                        .append(Component.text(minigame.getDescription() + "\n\n").color(NamedTextColor.GRAY))
                        .append(Component.text("Plays this session: ").color(NamedTextColor.WHITE))
                        .append(Component.text(GameManager.minigamePlays.get(minigame).toString()).color(NamedTextColor.AQUA))
                        .append(Component.text("\nFlags: ").color(NamedTextColor.WHITE)
                            .append(Component.text(String.valueOf(minigame.getFlags().size())).color(NamedTextColor.AQUA))
                                .clickEvent(ClickEvent.runCommand("/flags " + minigame.getId()))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to view flags.")))
                        .append(Component.text("\n-------------------------------------").color(NamedTextColor.AQUA))));
            } else {
                commandSender.sendMessage(Component.text("❓ ").append(Component.text("Invalid Minigame.").color(Colors.RED)));
            }
        } else {
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /minigame <id>").color(Colors.RED)));
        }

        return true;
    }
}
