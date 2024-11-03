package xyz.devcmb.cmr.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;

public class ScoreboardManager {
    public static BukkitRunnable updateScoreboard = null;
    public static void initialize(Player player){
        updateScoreboard = new BukkitRunnable() {
            @Override
            public void run() {
                if(GameManager.intermission) {
                    if((CmbMinigamesRandom.DeveloperMode ? (Bukkit.getOnlinePlayers().isEmpty()) : (Bukkit.getOnlinePlayers().size() < 2))) {
                        NotEnoughPlayersScoreboard.displayLobbyScoreboard(player);
                    } else {
                        StartingSoonScoreboard.displayStartingSoonScoreboard(player);
                    }
                } else {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
        };
        updateScoreboard.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 5);
    }
}
