package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public interface HandledScoreboard {
    Scoreboard getScoreboard(Player player);
}
