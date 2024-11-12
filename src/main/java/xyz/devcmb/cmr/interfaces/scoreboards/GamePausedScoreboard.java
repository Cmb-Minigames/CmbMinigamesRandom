package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.Objects;

public class GamePausedScoreboard implements HandledScoreboard {
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), ChatColor.GOLD + ChatColor.BOLD.toString() + "Cmb Minigames" + ChatColor.WHITE + " | " + ChatColor.GRAY + "v" + CmbMinigamesRandom.getPlugin().getDescription().getVersion());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank = objective.getScore(" ");
        blank.setScore(1);

        Score players = objective.getScore(ChatColor.WHITE + "\u1F46 Players: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size());
        players.setScore(3);

        Score waiting = objective.getScore("❓ Game is currently paused.");
        waiting.setScore(2);

        Score blank3 = objective.getScore("  ");
        blank3.setScore(4);

        return board;
    }
}
