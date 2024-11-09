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
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;
import xyz.devcmb.cmr.interfaces.scoreboards.minigames.CTFScoreboard;

import java.util.HashMap;
import java.util.Map;

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
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
        };
        updateScoreboard.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 5);
    }

    public static void registerAllScoreboards() {
        CaptureTheFlagController ctfController = (CaptureTheFlagController) GameManager.getMinigameByName("Capture the Flag");
        scoreboards.put("NotEnoughPlayers", new NotEnoughPlayersScoreboard());
        scoreboards.put("StartingSoon", new StartingSoonScoreboard());
        scoreboards.put("GamePaused", new GamePausedScoreboard());
        scoreboards.put("ctf", new CTFScoreboard(ctfController));
    }

    public static void displayScoreboardFromName(Player player, String scoreboard) {
        HandledScoreboard handledScoreboard = scoreboards.get(scoreboard);
        if (handledScoreboard != null) {
            sendScoreboardAlongDefaults(player, handledScoreboard.getScoreboard());
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public static void sendScoreboardAlongDefaults(Player player, Scoreboard board) {
        player.setScoreboard(mergeScoreboards(board));
    }

    public static Scoreboard mergeScoreboards(Scoreboard... boards) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
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
                    newObjective = merged.registerNewObjective(objective.getName(), objective.getCriteria(), objective.getDisplayName());
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