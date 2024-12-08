package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.TeleportersController;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class for the Teleporters minigame scoreboard
 */
public class TeleportersScoreboard implements HandledScoreboard {
    TeleportersController teleportersController;

    public TeleportersScoreboard(TeleportersController controller){
        teleportersController = controller;
    }

    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), " ".repeat(5) + ChatColor.YELLOW + ChatColor.BOLD + "Teleporters" + " ".repeat(5));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Map<Player, Integer> playerLives = teleportersController.playerLives;
        Score blank1 = objective.getScore(" ");
        blank1.setScore(playerLives.size() + 4);

        Score eventTimer = objective.getScore("ὕ Next event: " + ChatColor.AQUA + (!teleportersController.eventActive ? Utilities.formatTime(teleportersController.eventTimer) : "Now!"));
        eventTimer.setScore(playerLives.size() + 3);

        Score kills = objective.getScore("⚔ Kills: " + ChatColor.AQUA + GameManager.kills.get(player));
        kills.setScore(playerLives.size() + 2);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(playerLives.size() + 1);

        AtomicInteger index = new AtomicInteger(playerLives.size());
        playerLives.forEach((p, lives) -> {
            if(p == player){
                Score playerLivesScore = objective.getScore(ChatColor.BOLD + "You: " + ChatColor.RESET + ChatColor.AQUA + lives);
                playerLivesScore.setScore(index.getAndDecrement());
                return;
            }

            Score playerLivesScore = objective.getScore(p.getName() + ": " + ChatColor.AQUA + lives);
            playerLivesScore.setScore(index.getAndDecrement());
        });

        Score blank2 = objective.getScore("  ");
        blank2.setScore(0);

        return board;
    }
}
