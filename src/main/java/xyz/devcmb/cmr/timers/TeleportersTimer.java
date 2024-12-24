package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.TeleportersController;
import xyz.devcmb.cmr.utils.Colors;

public class TeleportersTimer extends TimerSuper implements Timer {
    public TeleportersTimer() {
        super(-1, 20, (early) -> {
            TeleportersController controller = (TeleportersController) GameManager.getMinigameByName("Teleporters");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.endGame();
        });
    }
}
