package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * An interface for handled scoreboards
 */
public interface HandledScoreboard {
    /**
     * Get the scoreboard for a player
     * @param player The player to get the scoreboard for
     * @return The scoreboard for the player
     */
    Scoreboard getScoreboard(Player player);
}
