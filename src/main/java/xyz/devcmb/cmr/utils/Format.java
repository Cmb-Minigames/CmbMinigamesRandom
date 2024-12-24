package xyz.devcmb.cmr.utils;

import org.bukkit.entity.Player;

/**
 * A utility class to format player names
 */
public class Format {
    /**
     * Format a player's name with their prefix
     * @param player The player to format
     * @return The formatted player name
     */
    public static String formatPlayerName(Player player){
        String name = player.getName();
        name = getPrefix(player) + name;

        return name;
    }

    /**
     * Get the prefix of a player
     * @param player The player to get the prefix of
     * @return The prefix of the player
     */
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

    /**
     * Get the tab list priority of a user.
     * @param player The player to get the priority of
     */
    public static Integer getPriority(Player player){
        if(player.hasPermission("group.owner")) {
            return 1;
        } else if(player.hasPermission("group.developer")){
            return 2;
        } else if(player.hasPermission("group.moderator")){
            return 3;
        } else if(player.hasPermission("group.tester")){
            return 4;
        }

        return 5;
    }
}
