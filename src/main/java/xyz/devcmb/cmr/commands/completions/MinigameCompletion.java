package xyz.devcmb.cmr.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;

import java.util.ArrayList;
import java.util.List;

/**
 * A tab completer for the minigame command
 */
public class MinigameCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> minigames = new ArrayList<>();
            for (Minigame m : GameManager.minigames) {
                minigames.add(m.getId());
            }
            return minigames;
        }

        return null;
    }
}
