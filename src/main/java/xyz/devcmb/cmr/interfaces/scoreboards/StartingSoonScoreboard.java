package xyz.devcmb.cmr.interfaces.scoreboards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;

import java.util.Objects;

/**
 * A class for the starting soon scoreboard
 */
public class StartingSoonScoreboard implements HandledScoreboard {
    @SuppressWarnings("deprecation")
    @Override
    public Scoreboard getScoreboard(Player player) {
        if(GameManager.intermissionTimer == null) return null;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Cmb Minigames").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" | "))
                        .append(Component.text("v")
                                .append(Component.text(CmbMinigamesRandom.getPlugin().getDescription().getVersion())).color(NamedTextColor.GRAY))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank = objective.getScore(" ");
        blank.setScore(1);

        Score players = objective.getScore(ChatColor.WHITE + "\u1F46 Players: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size());
        players.setScore(3);

        Score startingIn = objective.getScore("ὕ Starting in " + ChatColor.AQUA + GameManager.intermissionTimer.getTime() + " seconds");
        startingIn.setScore(2);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(4);

        return board;
    }
}
