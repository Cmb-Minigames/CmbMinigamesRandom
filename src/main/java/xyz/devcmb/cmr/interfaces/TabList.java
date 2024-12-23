package xyz.devcmb.cmr.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.Format;

/**
 * An interfaces class for updating the tab list name of a player
 */
public class TabList {
    /**
     * Update the tab list name of a player
     * @param player The player to update the tab list name of
     */
    public static void updateTabListName(Player player){
        String prefix = Format.getPrefix(player);
        TextColor teamColor = GameManager.teamColors.get(player);
        if(prefix == null){
            prefix = "";
        }

        if(teamColor == null){
            teamColor = TextColor.color(0xFFFFFF);
        }

        player.playerListName(Component.text(prefix + player.getName()).color(teamColor));
    }
}
