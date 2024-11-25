package xyz.devcmb.cmr.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devcmb.cmr.cosmetics.CrateManager;

import java.util.ArrayList;
import java.util.List;

public class CrateCommandCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(CrateManager.crates.keySet());
        }

        return null;
    }
}
