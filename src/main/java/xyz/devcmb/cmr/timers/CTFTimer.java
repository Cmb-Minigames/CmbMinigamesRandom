package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;
import xyz.devcmb.cmr.utils.Colors;

public class CTFTimer extends TimerSuper implements Timer {
    public CTFTimer() {
        super(60 * 15, 20, (early) -> {
            CaptureTheFlagController controller = (CaptureTheFlagController) GameManager.getMinigameByName("Capture The Flag");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            if(controller.redScore > controller.blueScore) {
                controller.endGame("red");
            } else if(controller.blueScore > controller.redScore) {
                controller.endGame("blue");
            } else {
                controller.endGame("draw");
            }
        });
    }
}
