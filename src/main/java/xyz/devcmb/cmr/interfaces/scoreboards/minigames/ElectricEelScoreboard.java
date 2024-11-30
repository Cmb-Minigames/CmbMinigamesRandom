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

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
