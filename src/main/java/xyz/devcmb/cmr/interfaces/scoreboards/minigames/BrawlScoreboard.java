package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.BrawlController;

/**
 * A class for the Brawl minigame scoreboard
 */
public class BrawlScoreboard implements HandledScoreboard {
    private final BrawlController brawlController;
    public BrawlScoreboard(BrawlController brawlController){
        this.brawlController = brawlController;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(brawlController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), " ".repeat(5) + ChatColor.YELLOW + ChatColor.BOLD + "Brawl" + " ".repeat(5));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score playersLeft = objective.getScore("\u1F46 Players Left: " + ChatColor.AQUA + brawlController.players.size());
        playersLeft.setScore(5);

        Score kills = objective.getScore("⚔ Kills: " + ChatColor.AQUA + GameManager.kills.get(player));
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        return board;
    }
}
