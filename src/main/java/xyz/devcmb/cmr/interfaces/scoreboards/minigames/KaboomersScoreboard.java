package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.KaboomersController;
import xyz.devcmb.cmr.utils.Utilities;

public class KaboomersScoreboard implements HandledScoreboard {
    private final KaboomersController kaboomersController;
    public KaboomersScoreboard(KaboomersController kc){
        kaboomersController = kc;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), ChatColor.YELLOW + ChatColor.BOLD.toString() + "Kaboomers");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score timeLeft = objective.getScore("ὕ Time Left: " + ChatColor.AQUA + Utilities.formatTime(kaboomersController.timeLeft));
        timeLeft.setScore(5);

        Score kills = objective.getScore("⚔ Kills: " + ChatColor.AQUA + GameManager.kills.get(player));
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score red = objective.getScore(ChatColor.RED + "Red" + ChatColor.RESET + ": " + ChatColor.AQUA + kaboomersController.redBlocks.size());
        red.setScore(2);

        Score blue = objective.getScore(ChatColor.BLUE + "Blue" + ChatColor.RESET + ": " + ChatColor.AQUA + kaboomersController.blueBlocks.size());
        blue.setScore(1);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
