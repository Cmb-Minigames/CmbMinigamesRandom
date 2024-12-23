package xyz.devcmb.cmr.interfaces.scoreboards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Colors;

import java.util.Objects;

/**
 * A class for the game paused scoreboard
 */
public class GamePausedScoreboard implements HandledScoreboard {
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Cmb Minigames").color(Colors.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" | "))
                        .append(Component.text("v")
                            .append(Component.text(CmbMinigamesRandom.getPlugin().getDescription().getVersion())).color(Colors.GRAY))
                        .append(Component.text(" ".repeat(5))));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank = objective.getScore(" ");
        blank.setScore(1);

        Score players = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("\u1F46 Players: ").append(Component.text(Bukkit.getOnlinePlayers().size()).color(Colors.AQUA))
        ));

        players.setScore(3);

        Score waiting = objective.getScore("❓ Game is currently paused.");
        waiting.setScore(2);

        Score blank3 = objective.getScore("  ");
        blank3.setScore(4);

        return board;
    }
}
