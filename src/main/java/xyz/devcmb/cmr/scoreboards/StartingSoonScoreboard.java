package xyz.devcmb.cmr.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;

public class StartingSoonScoreboard {
    public static void displayStartingSoonScoreboard(Player player){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", "dummy", ChatColor.GOLD + ChatColor.BOLD.toString() + "Cmb Minigames" + ChatColor.WHITE + " | " + ChatColor.GRAY + "v" + CmbMinigamesRandom.getPlugin().getDescription().getVersion());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank = objective.getScore(" ");
        blank.setScore(1);

        Score players = objective.getScore(ChatColor.WHITE + "\u1F46 Players: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size());
        players.setScore(3);

        Score startingIn = objective.getScore("ὕ Starting in " + ChatColor.AQUA + GameManager.timeLeft + " seconds");
        startingIn.setScore(2);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(4);

        player.setScoreboard(board);
    }
}
