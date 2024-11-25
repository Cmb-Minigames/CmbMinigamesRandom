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
        if(player.hasPermission("group.owner")) {
            prefix = "\uE000 " + prefix;
        } else if(player.hasPermission("group.developer")){
            prefix = "\uE00D " + prefix;
        } else if(player.hasPermission("group.moderator")){
            prefix = "\uE002 " + prefix;
        } else if(player.hasPermission("group.tester")){
            prefix = "\uE001 " + prefix;
        }

        return prefix;
    }

    public static Integer getPriority(Player player){
        if(player.hasPermission("group.owner")) {
            return 4;
        } else if(player.hasPermission("group.developer")){
            return 3;
        } else if(player.hasPermission("group.moderator")){
            return 2;
        } else if(player.hasPermission("group.tester")){
            return 1;
        }

        return 0;
    }
}
