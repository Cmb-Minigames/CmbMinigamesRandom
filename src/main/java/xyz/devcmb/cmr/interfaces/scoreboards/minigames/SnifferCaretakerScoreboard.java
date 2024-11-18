package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.KaboomersController;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;
import xyz.devcmb.cmr.utils.Utilities;

public class SnifferCaretakerScoreboard implements HandledScoreboard {
    private final SnifferCaretakerController snifferCaretakerController;
    public SnifferCaretakerScoreboard(SnifferCaretakerController kc){
        snifferCaretakerController = kc;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard board = scoreboardManager.getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"), ChatColor.YELLOW + ChatColor.BOLD.toString() + "Sniffer Caretaker");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        return board;
    }
}
