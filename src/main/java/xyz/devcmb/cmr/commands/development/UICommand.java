package xyz.devcmb.cmr.commands.development;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmr.utils.Colors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A command for opening a custom inventory for custom interface development
 */
public class UICommand implements CommandExecutor {
    private final Map<Player, Map<Integer, Inventory>> playerInventories = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Only players can use this command").color(Colors.RED)));
            return true;
        }

        if(args.length < 2){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Usage: /ui <slots> <title>").color(Colors.RED)));
            return true;
        }

        if(!args[0].matches("[0-9]+") || Integer.parseInt(args[0]) % 9 != 0 || Integer.parseInt(args[0]) > 54){
            commandSender.sendMessage(Component.text("❓ ").append(Component.text("Invalid number of slots, must be a multiple of nine below 54.").color(Colors.RED)));
            return true;
        }

        int slots = Integer.parseInt(args[0]);
        Component title = Component.text(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

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
