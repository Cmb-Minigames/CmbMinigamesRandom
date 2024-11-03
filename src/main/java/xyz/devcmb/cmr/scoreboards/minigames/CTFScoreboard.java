package xyz.devcmb.cmr.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;

public class CTFScoreboard {
    public static void displayCTFScoreboard(Player player, CaptureTheFlagController ctfController){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", "dummy", ChatColor.YELLOW + ChatColor.BOLD.toString() + "Capture the Flag");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score RED = objective.getScore(ChatColor.RED + ChatColor.BOLD.toString() + "RED");
        RED.setScore(5);


        String redPoints = (ctfController.redScore > 0 ? "᠍" : "\u00AD") + " " +
                (ctfController.redScore > 1 ? "᠍" : "\u00AD") + " " +
                (ctfController.redScore > 2 ? "᠍" : "\u00AD");

        Score RedScore = objective.getScore(redPoints);
        RedScore.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score BLUE = objective.getScore(ChatColor.BLUE + ChatColor.BOLD.toString() + "BLUE");
        BLUE.setScore(2);

        String bluePoints = (ctfController.blueScore > 0 ? "\u180E" : "᠋") + " " +
                (ctfController.blueScore > 1 ? "\u180E" : "᠋") + " " +
                (ctfController.blueScore > 2 ? "\u180E" : "᠋");

        Score BlueScore = objective.getScore(bluePoints);
        BlueScore.setScore(1);

        player.setScoreboard(board);
    }
}
