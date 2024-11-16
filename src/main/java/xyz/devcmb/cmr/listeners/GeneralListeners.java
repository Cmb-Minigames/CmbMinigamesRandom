package xyz.devcmb.cmr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Format;

import java.lang.reflect.Field;

public class GeneralListeners implements Listener {
    private CommandMap commandMap;
    public GeneralListeners() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to get command map.");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
        Command minecraftCommand = commandMap.getCommand(command);

        if (pluginCommand == null && minecraftCommand == null) {
            event.getPlayer().sendMessage("❓ " + ChatColor.RED + "This command does not exist.");
            event.setCancelled(true);
        } else if(event.getMessage().startsWith("/stop") || event.getMessage().startsWith("/reload")) {
            event.getPlayer().sendMessage("❓ " + ChatColor.RED + "No.");
            event.setCancelled(true);
        } else if (pluginCommand != null) {
            String permission = pluginCommand.getPermission();
            if (permission != null && !event.getPlayer().hasPermission(permission)) {
                event.getPlayer().sendMessage("❓ " + ChatColor.RED + "You do not have permission to use this command.");
                event.setCancelled(true);
            }
        } else {
            String permission = minecraftCommand.getPermission();
            if (permission != null && !event.getPlayer().hasPermission(permission)) {
                event.getPlayer().sendMessage("❓ " + ChatColor.RED + "You do not have permission to use this command.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        if(event.getMessage().contains("https://")){
            event.getPlayer().sendMessage("❓ " + ChatColor.RED + "You cannot send links in chat.");
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setFormat(Format.formatPlayerName(player) + ChatColor.WHITE + ": " + message);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            }, 3); // This is required to prevent the unlimited blocks from giving you it back
        }
    }
}
