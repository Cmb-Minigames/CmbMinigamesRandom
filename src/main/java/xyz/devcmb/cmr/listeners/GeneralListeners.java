package xyz.devcmb.cmr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.Field;
import java.util.Objects;

public class GeneralListeners implements Listener {
    private CommandMap commandMap;
    public GeneralListeners() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
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
        } else if (pluginCommand != null && !event.getPlayer().hasPermission(Objects.requireNonNull(pluginCommand.getPermission()))) {
            event.getPlayer().sendMessage("❓ " + ChatColor.RED + "You do not have permission to use this command.");
            event.setCancelled(true);
        } else if (minecraftCommand != null && !event.getPlayer().hasPermission(Objects.requireNonNull(minecraftCommand.getPermission()))) {
            event.getPlayer().sendMessage("❓ " + ChatColor.RED + "You do not have permission to use this command.");
            event.setCancelled(true);
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
        String playerName = player.getName();
        String message = event.getMessage();

        if(player.hasPermission("group.owner")){
            event.setFormat("\uE000 " + ChatColor.RED + playerName + ChatColor.WHITE + " » " + message);
        } else if(player.hasPermission("group.moderator")){
            event.setFormat("\uE002 " + ChatColor.LIGHT_PURPLE + playerName + ChatColor.WHITE + " » " + message);
        } else if(player.hasPermission("group.tester")){
            event.setFormat("\uE001 " + ChatColor.BLUE + playerName + ChatColor.WHITE + " » " + message);
        } else {
            event.setFormat(playerName + " » " + message);
        }
    }
}
