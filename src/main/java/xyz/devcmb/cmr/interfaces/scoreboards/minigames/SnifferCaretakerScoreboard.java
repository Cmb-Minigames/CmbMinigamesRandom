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
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;

/**
 * A class for the Sniffer Caretaker minigame scoreboard
 */
public class SnifferCaretakerScoreboard implements HandledScoreboard {
    private final SnifferCaretakerController snifferCaretakerController;
    public SnifferCaretakerScoreboard(SnifferCaretakerController kc){
        snifferCaretakerController = kc;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(snifferCaretakerController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Sniffer Caretaker").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score kills = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("⚔ Kills: ").append(Component.text(GameManager.kills.get(player).toString()).color(NamedTextColor.AQUA)))
        );

        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        Score red = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("Red Sniffer", NamedTextColor.RED).append(Component.text(": ")).append(Component.text(snifferCaretakerController.redSnifferHappiness + "☺").color(NamedTextColor.AQUA))));
        red.setScore(2);

        Score blue = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("Blue Sniffer", NamedTextColor.BLUE).append(Component.text(": ")).append(Component.text(snifferCaretakerController.blueSnifferHappiness + " ☺").color(NamedTextColor.AQUA))));
        blue.setScore(1);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(0);

        return board;
    }
}
