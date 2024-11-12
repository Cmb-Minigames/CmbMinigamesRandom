package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.utils.Format;

import java.util.Objects;

public class TabListPrefix implements HandledScoreboard {
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = Objects.requireNonNull(manager).getNewScoreboard();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String prefix = Format.getPrefix(onlinePlayer);
            Team team = board.getTeam(prefix);
            if (team == null) {
                team = board.registerNewTeam(prefix);
                team.setPrefix(prefix);
            }
            team.addEntry(onlinePlayer.getName());
        }

        return board;
    }
}
