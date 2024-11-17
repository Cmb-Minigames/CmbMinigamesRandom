package xyz.devcmb.cmr.interfaces;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.Format;

public class TabList {
    public static void updateTabListName(Player player){
        String prefix = Format.getPrefix(player);
        ChatColor teamColor = GameManager.teamColors.get(player);

        player.setPlayerListName(prefix + teamColor + player.getName());
    }
}
