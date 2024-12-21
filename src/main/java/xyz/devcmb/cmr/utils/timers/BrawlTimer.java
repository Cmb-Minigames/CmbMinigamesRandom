package xyz.devcmb.cmr.utils.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.BrawlController;

public class BrawlTimer extends TimerSuper implements Timer {
    public BrawlTimer() {
        super(-1, 20, (early) -> {
            BrawlController controller = (BrawlController) GameManager.getMinigameByName("Brawl");
            if(controller == null) return;

            controller.allPlayers.forEach(player -> {
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);

                if(controller.players.contains(player)){
                    player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                } else {
                    player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                }
            });

            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), controller::stop, 20 * 8);
        });
    }
}
