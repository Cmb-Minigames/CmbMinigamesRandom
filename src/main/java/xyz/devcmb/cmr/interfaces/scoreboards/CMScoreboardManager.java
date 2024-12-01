package xyz.devcmb.cmr.interfaces.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.minigames.*;
import xyz.devcmb.cmr.minigames.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CMScoreboardManager {
    public static BukkitRunnable updateScoreboard = null;
    public static Map<String, HandledScoreboard> scoreboards = new HashMap<>();

    public static void initialize(Player player) {
        updateScoreboard = new BukkitRunnable() {
            @Override
            public void run() {
                if (GameManager.intermission) {
                    if ((CmbMinigamesRandom.DeveloperMode ? (Bukkit.getOnlinePlayers().isEmpty()) : (Bukkit.getOnlinePlayers().size() < 2))) {
                        displayScoreboardFromName(player, "NotEnoughPlayers");
                    } else {
                        displayScoreboardFromName(player, "StartingSoon");
                    }
                } else if (GameManager.ingame) {
                    GameManager.currentMinigame.updateScoreboard(player);
                } else if (GameManager.paused) {
                    displayScoreboardFromName(player, "GamePaused");
                } else {
                    sendScoreboardAlongDefaults(player, Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
                }
            }
        };
        updateScoreboard.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 5);
    }

    public static void registerAllScoreboards() {
        CaptureTheFlagController ctfController = (CaptureTheFlagController) GameManager.getMinigameByName("Capture the Flag");
        KaboomersController kaboomersController = (KaboomersController) GameManager.getMinigameByName("Kaboomers");
        BrawlController brawlController = (BrawlController) GameManager.getMinigameByName("Brawl");
        SnifferCaretakerController snifferCaretakerController = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
        CookingChaosController cookingChaosController = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
        TeleportersController teleportersController = (TeleportersController) GameManager.getMinigameByName("Teleporters");
        scoreboards.put("NotEnoughPlayers", new NotEnoughPlayersScoreboard());
        scoreboards.put("StartingSoon", new StartingSoonScoreboard());
        scoreboards.put("GamePaused", new GamePausedScoreboard());
        scoreboards.put("ctf", new CTFScoreboard(ctfController));
        scoreboards.put("kaboomers", new KaboomersScoreboard(kaboomersController));
        scoreboards.put("brawl", new BrawlScoreboard(brawlController));
        scoreboards.put("sniffercaretaker", new SnifferCaretakerScoreboard(snifferCaretakerController));
        scoreboards.put("cookingchaos", new CookingChaosScoreboard(cookingChaosController));
        scoreboards.put("teleporters", new TeleportersScoreboard(teleportersController));
    }

    public static void displayScoreboardFromName(Player player, String scoreboard) {
        HandledScoreboard handledScoreboard = scoreboards.get(scoreboard);
        if (handledScoreboard != null) {
            sendScoreboardAlongDefaults(player, handledScoreboard.getScoreboard(player));
        } else {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
        }
    }

    public static void sendScoreboardAlongDefaults(Player player, Scoreboard board) {
        // no custom ones atm
        player.setScoreboard(board);
    }

    public static Scoreboard mergeScoreboards(Scoreboard... boards) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard merged = scoreboardManager.getNewScoreboard();

        for (Scoreboard board : boards) {
            board.getTeams().forEach(team -> {
                org.bukkit.scoreboard.Team newTeam = merged.registerNewTeam(team.getName());
                newTeam.setPrefix(team.getPrefix());
                newTeam.setSuffix(team.getSuffix());
                newTeam.setColor(team.getColor());
                newTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, team.getOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY));
                newTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, team.getOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE));
                newTeam.setOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY, team.getOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY));
                team.getEntries().forEach(newTeam::addEntry);
            });

            board.getObjectives().forEach(objective -> {
                Objective newObjective = merged.getObjective(objective.getName());
                if (newObjective == null) {
                    newObjective = merged.registerNewObjective(objective.getName(), objective.getTrackedCriteria(), objective.getDisplayName(), objective.getRenderType());
                    newObjective.setDisplaySlot(objective.getDisplaySlot());
                }
                for (String entry : board.getEntries()) {
                    Score score = objective.getScore(entry);
                    newObjective.getScore(entry).setScore(score.getScore());
                }
            });
        }

        return merged;
    }
}