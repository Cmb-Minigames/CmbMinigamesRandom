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
import xyz.devcmb.cmr.minigames.TeleportersController;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class for the Teleporters minigame scoreboard
 */
public class TeleportersScoreboard implements HandledScoreboard {
    TeleportersController teleportersController;

    public TeleportersScoreboard(TeleportersController controller){
        teleportersController = controller;
    }

    @Override
    public Scoreboard getScoreboard(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getNewScoreboard();

        if(teleportersController.timer == null) return board;

        Objective objective = board.registerNewObjective("info", Criteria.create("dummy"),
                Component.text(" ".repeat(5))
                        .append(Component.text("Teleporters").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" ".repeat(5))));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Map<Player, Integer> playerLives = teleportersController.playerLives;
        Score blank1 = objective.getScore(" ");
        blank1.setScore(playerLives.size() + 4);

        Score eventTimer = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("ὕ Next event: ").append(Component.text(
                        (!teleportersController.eventActive ? Utilities.formatTime(teleportersController.eventTimer) : "Now!")
                ).color(NamedTextColor.AQUA))));
        eventTimer.setScore(playerLives.size() + 3);

        Score kills = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                Component.text("⚔ Kills: ").append(Component.text(GameManager.kills.get(player).toString()).color(NamedTextColor.AQUA)))
        );
        kills.setScore(playerLives.size() + 2);

        Score blank3 = objective.getScore("   ");
        blank3.setScore(playerLives.size() + 1);

        AtomicInteger index = new AtomicInteger(playerLives.size());
        playerLives.forEach((p, lives) -> {
            if(p == player){
                Score playerLivesScore = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                    Component.text("You: ").decorate(TextDecoration.BOLD).append(Component.text(lives).color(NamedTextColor.AQUA))
                ));
                playerLivesScore.setScore(index.getAndDecrement());
                return;
            }

            Score playerLivesScore = objective.getScore(LegacyComponentSerializer.legacySection().serialize(
                    Component.text(p.getName() + ":").append(Component.text(lives).color(NamedTextColor.AQUA))
            ));
            playerLivesScore.setScore(index.getAndDecrement());
        });

        Score blank2 = objective.getScore("  ");
        blank2.setScore(0);

        return board;
    }
}
