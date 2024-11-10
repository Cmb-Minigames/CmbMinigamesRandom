package xyz.devcmb.cmr.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Format {
    public static String formatPlayerName(Player player){
        String name = player.getName();
        name = getPrefix(player) + name + ChatColor.RESET;

        return name;
    }

    public static String getPrefix(Player player){
        String prefix = "";
        if(player.hasPermission("group.owner")){
            prefix = "\uE000 " + prefix;
        } else if(player.hasPermission("group.moderator")){
            prefix = "\uE002 " + prefix;
        } else if(player.hasPermission("group.tester")){
            prefix = "\uE001 " + prefix;
        }

        return prefix;
    }
}
