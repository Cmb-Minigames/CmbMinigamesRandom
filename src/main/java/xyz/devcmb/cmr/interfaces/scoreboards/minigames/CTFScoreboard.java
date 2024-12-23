package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.utils.Utilities;

/**
 * A class for the Capture the Flag minigame scoreboard
 */
public class CTFScoreboard implements HandledScoreboard {
    private final CaptureTheFlagController ctfController;

    public CTFScoreboard(CaptureTheFlagController ctfController) {
        this.ctfController = ctfController;
    }

    @SuppressWarnings("deprecation") // this one would be PAINFUL to fix
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(ctfController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Capture the Flag").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore("   ");
        blank1.setScore(6);

        Score time = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("ὕ Time Left: ")
                        .append(Component.text(Utilities.formatTime(ctfController.timer.getTime())).color(NamedTextColor.AQUA))
        ));
        time.setScore(5);

        Score blank3 = objective.getScore("  ");
        blank3.setScore(4);

        String redPoints = (ctfController.redScore > 0 ? "[" + ChatColor.RED + "O" + ChatColor.RESET + "]" : "[ ]") + " " +
                (ctfController.redScore > 1 ? "[" + ChatColor.RED + "O" + ChatColor.RESET + "]" : "[ ]") + " " +
                (ctfController.redScore > 2 ? "[" + ChatColor.RED + "O" + ChatColor.RESET + "]" : "[ ]");

        Score RED = objective.getScore(ChatColor.RED + ChatColor.BOLD.toString() + "RED: " + ChatColor.RESET + redPoints);
        RED.setScore(3);

        String bluePoints = (ctfController.blueScore > 0 ? "[" + ChatColor.AQUA + "O" + ChatColor.RESET + "]" : "[ ]") + " " +
                (ctfController.blueScore > 1 ? "[" + ChatColor.AQUA + "O" + ChatColor.RESET + "]" : "[ ]") + " " +
                (ctfController.blueScore > 2 ? "[" + ChatColor.AQUA + "O" + ChatColor.RESET + "]" : "[ ]");

        Score BLUE = objective.getScore(ChatColor.AQUA + ChatColor.BOLD.toString() + "BLUE: " + ChatColor.RESET + bluePoints);
        BLUE.setScore(2);

        Score blank2 = objective.getScore(" ");
        blank2.setScore(1);

        return board;
    }
}
