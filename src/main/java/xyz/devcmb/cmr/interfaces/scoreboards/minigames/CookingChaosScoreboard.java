package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.CookingChaosController;
import xyz.devcmb.cmr.utils.Utilities;

/**
 * A class for the Cooking Chaos minigame scoreboard
 */
public class CookingChaosScoreboard implements HandledScoreboard {
    CookingChaosController controller;

    public CookingChaosScoreboard(CookingChaosController controller) {
        this.controller = controller;
    }

    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(controller.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), " ".repeat(5) + ChatColor.YELLOW + ChatColor.BOLD + "Cooking Chaos" + " ".repeat(5));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score timeLeft = objective.getScore("ὕ Time Left: " + ChatColor.AQUA + Utilities.formatTime(controller.timer.getTime()));
        timeLeft.setScore(5);

        Score kills = objective.getScore("⚔ Kills: " + ChatColor.AQUA + GameManager.kills.get(player));
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score red = objective.getScore(ChatColor.RED + "Red" + ChatColor.RESET + ": " + ChatColor.AQUA + controller.redScore);
        red.setScore(2);

        Score blue = objective.getScore(ChatColor.BLUE + "Blue" + ChatColor.RESET + ": " + ChatColor.AQUA + controller.blueScore);
        blue.setScore(1);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
