package xyz.devcmb.cmr.commands.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;
import xyz.devcmb.cmr.utils.Colors;

/**
 * A command for re-fetching all cosmetics and crates from the database
 */
public class ReloadCosmeticsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        commandSender.sendMessage(Component.text("Attempting to reload cosmetics...").color(Colors.BLUE));
        try {
            CosmeticManager.cosmetics.clear();
            CrateManager.crates.clear();

            CosmeticManager.registerAllCosmetics();
            CrateManager.registerAllCrates();
            commandSender.sendMessage(Component.text("Cosmetics reloaded successfully.").color(Colors.GREEN));
        } catch(Exception e){
            Component text = Component.text("❓ ")
                    .append(Component.text("An error occurred while reloading cosmetics: " + e.getMessage()).color(Colors.RED));

            commandSender.sendMessage(text);
        }
        return true;
    }
}
