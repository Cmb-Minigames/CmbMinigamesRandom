package xyz.devcmb.cmr.commands.cosmetics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.cosmetics.CrateManager;

public class ReloadCosmeticsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        commandSender.sendMessage(ChatColor.BLUE + "Attempting to reload cosmetics...");
        try {
            CosmeticManager.cosmetics.clear();
            CrateManager.crates.clear();

            CosmeticManager.registerAllCosmetics();
            CrateManager.registerAllCrates();
            commandSender.sendMessage(ChatColor.GREEN + "Cosmetics reloaded successfully.");
        } catch(Exception e){
            commandSender.sendMessage("❓ " + ChatColor.RED + "An error occurred while reloading cosmetics: " + e.getMessage());
        }
        return true;
    }
}
