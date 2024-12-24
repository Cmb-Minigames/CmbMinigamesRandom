package xyz.devcmb.cmr.interfaces.scoreboards.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.HandledScoreboard;
import xyz.devcmb.cmr.minigames.KaboomersController;
import xyz.devcmb.cmr.utils.Utilities;

/**
 * A class for the Kaboomers minigame scoreboard
 */
public class KaboomersScoreboard implements HandledScoreboard {
    private final KaboomersController kaboomersController;
    public KaboomersScoreboard(KaboomersController kc){
        kaboomersController = kc;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(kaboomersController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Kaboomers").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score timeLeft = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("ὕ Time Left: ")
                        .append(Utilities.formatTime(kaboomersController.timer.getTime()).color(NamedTextColor.AQUA))
        ));
        timeLeft.setScore(5);

        Score kills = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("⚔ Kills: ").append(Component.text(GameManager.kills.get(player).toString()).color(NamedTextColor.AQUA)))
        );
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score red = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("Red", NamedTextColor.RED).append(Component.text(": ")).append(Component.text(kaboomersController.redBlocks.size()).color(NamedTextColor.AQUA))));
        red.setScore(2);

        Score blue = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("Blue", NamedTextColor.BLUE).append(Component.text(": ")).append(Component.text(kaboomersController.redBlocks.size()).color(NamedTextColor.AQUA))));
        blue.setScore(1);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
