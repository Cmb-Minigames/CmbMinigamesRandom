package xyz.devcmb.cmr.interfaces;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.Format;

public class TabList {
    public static void updateTabListName(Player player){
        String prefix = Format.getPrefix(player);
        Integer priority = Format.getPriority(player);
        ChatColor teamColor = GameManager.teamColors.get(player);
        if(prefix == null){
            prefix = "";
        }

        if(teamColor == null){
            teamColor = ChatColor.WHITE;
        }

        player.setPlayerListName(ChatColor.values()[priority].toString() + ChatColor.RESET + prefix + teamColor + player.getName());
    }
}
