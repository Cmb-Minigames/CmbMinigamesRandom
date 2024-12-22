package xyz.devcmb.cmr.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.KaboomersController;

public class KaboomersTimer extends TimerSuper implements Timer {
    public KaboomersTimer() {
        super(120, 20, (early) -> {
            KaboomersController controller = (KaboomersController) GameManager.getMinigameByName("Kaboomers");
            if(controller == null) return;
            if(early) Bukkit.broadcastMessage(ChatColor.GREEN + "The game has been ended early by an administrator");

            controller.endGame();
        });
    }
}
