package xyz.devcmb.cmr.commands.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;

public class SelectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 1){
            Minigame minigame = GameManager.getMinigameById(args[0]);
            if(minigame != null){
                GameManager.selectedMinigame = minigame;
                Bukkit.broadcastMessage(ChatColor.GREEN + "✅ " + minigame.getName() + " has been selected as the next minigame by an administrator.");
            } else {
                sender.sendMessage("❓ Invalid minigame.");
            }
        } else {
            sender.sendMessage("❓ Invalid arguments. Usage: /select <minigame>");
        }
        return true;
    }
}
