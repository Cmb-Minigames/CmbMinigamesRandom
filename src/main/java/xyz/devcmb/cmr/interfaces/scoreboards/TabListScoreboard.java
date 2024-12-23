package xyz.devcmb.cmr.interfaces.scoreboards;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.utils.Format;

/**
 * A class for the tab list scoreboard
 */
public class TabListScoreboard implements HandledScoreboard{
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board = manager.getNewScoreboard();

        Bukkit.getOnlinePlayers().forEach(plr -> {
            Integer priority = Format.getPriority(plr);
            Team team = board.registerNewTeam(priority + plr.getName());
            team.color((NamedTextColor) GameManager.teamColors.get(plr));
            team.addEntry(plr.getName());
        });

        return board;
    }
}
