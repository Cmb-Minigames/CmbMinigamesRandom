package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;

import java.util.Objects;

/**
 * A class for the starting soon scoreboard
 */
public class StartingSoonScoreboard implements HandledScoreboard {
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), " ".repeat(5) + ChatColor.GOLD + ChatColor.BOLD + "Cmb Minigames" + ChatColor.WHITE + " | " + ChatColor.GRAY + "v" + CmbMinigamesRandom.getPlugin().getDescription().getVersion() + " ".repeat(5));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank = objective.getScore(" ");
        blank.setScore(1);

        Score players = objective.getScore(ChatColor.WHITE + "\u1F46 Players: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size());
        players.setScore(3);

        Score startingIn = objective.getScore("ὕ Starting in " + ChatColor.AQUA + GameManager.timeLeft + " seconds");
        startingIn.setScore(2);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(4);

        return board;
    }
}
