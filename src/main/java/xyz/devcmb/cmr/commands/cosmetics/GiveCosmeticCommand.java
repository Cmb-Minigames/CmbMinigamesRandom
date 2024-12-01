package xyz.devcmb.cmr.commands.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;

/**
 * A command for giving a cosmetic to a player
 */
public class GiveCosmeticCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("❓ " + ChatColor.RED + "Usage: /cosmetic <player> <cosmetic>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("❓" + ChatColor.RED + " Player not found.");
            return true;
        }

        ItemStack cosmeticStack = CosmeticManager.cosmetics.get(args[1]);
        if(cosmeticStack == null || cosmeticStack.getItemMeta() == null){
            commandSender.sendMessage("❓" + ChatColor.RED + " Cosmetic not found.");
            return true;
        }

        Database.giveCosmetic(player, args[1]);
        commandSender.sendMessage(ChatColor.GREEN + "Gave " + ChatColor.WHITE + Format.formatPlayerName(player) + " the " + cosmeticStack.getItemMeta().getItemName() + ChatColor.GREEN + " cosmetic.");

        return true;
    }
}
