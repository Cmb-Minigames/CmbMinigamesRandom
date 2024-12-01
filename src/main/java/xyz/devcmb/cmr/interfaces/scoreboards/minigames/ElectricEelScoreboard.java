package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.ElectricEelController;

public class ElectricEelScoreboard implements HandledScoreboard {
    private final ElectricEelController electricEelController;
    public ElectricEelScoreboard(ElectricEelController kc){
        electricEelController = kc;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), ChatColor.YELLOW + ChatColor.BOLD.toString() + "Electric Eel");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score kills = objective.getScore("⚔ Kills: " + ChatColor.AQUA + GameManager.kills.get(player));
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score red = objective.getScore(ChatColor.RED + "Polluters" + ChatColor.RESET + ": " + ChatColor.AQUA + electricEelController.redUranium);
        red.setScore(2);

        Score blue = objective.getScore(ChatColor.BLUE + "Electric Eels" + ChatColor.RESET + ": " + ChatColor.AQUA + electricEelController.blueUranium);
        blue.setScore(1);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
