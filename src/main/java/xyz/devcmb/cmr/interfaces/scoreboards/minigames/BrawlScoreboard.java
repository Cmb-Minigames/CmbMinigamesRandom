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
import xyz.devcmb.cmr.minigames.BrawlController;

/**
 * A class for the Brawl minigame scoreboard
 */
public class BrawlScoreboard implements HandledScoreboard {
    private final BrawlController brawlController;
    public BrawlScoreboard(BrawlController brawlController){
        this.brawlController = brawlController;
    }
    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(brawlController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Brawl").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blank1 = objective.getScore(" ");
        blank1.setScore(6);

        Score playersLeft = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("\u1F46 Players Left: ")
                        .append(Component.text(brawlController.players.size()).color(NamedTextColor.AQUA)))
        );
        playersLeft.setScore(5);

        Score kills = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("⚔ Kills: ").append(Component.text(GameManager.kills.get(player).toString()).color(NamedTextColor.AQUA)))
        );
        kills.setScore(4);

        Score blank2 = objective.getScore("  ");
        blank2.setScore(3);

        return board;
    }
}
