package xyz.devcmb.cmr.commands.development;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UICommand implements CommandExecutor {
    private final Map<Player, Map<Integer, Inventory>> playerInventories = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage(ChatColor.RED + "Only players can execute this command");
            return true;
        }

        if(args.length < 2){
            player.sendMessage("❓ " + ChatColor.RED + "Usage: /ui <slots> <title>");
            return true;
        }

        if(!args[0].matches("[0-9]+") || Integer.parseInt(args[0]) % 9 != 0 || Integer.parseInt(args[0]) > 54){
            player.sendMessage("❓ " + ChatColor.RED + "Invalid number of slots. It must be a multiple of 9 up to 54.");
            return true;
        }

        int slots = Integer.parseInt(args[0]);
        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Inventory inventory;

        if(playerInventories.containsKey(player) && playerInventories.get(player).containsKey(slots)){
            Inventory oldInventory = playerInventories.get(player).get(slots);
            inventory = Bukkit.createInventory(player, slots, title);

            for (int i = 0; i < slots; i++) {
                ItemStack item = oldInventory.getItem(i);
                if (item != null) {
                    inventory.setItem(i, item);
                }
            }
        } else {
            inventory = Bukkit.createInventory(player, slots, title);
        }

        player.openInventory(inventory);
        playerInventories.put(player, Map.of(slots, inventory));

        return true;
    }
}
