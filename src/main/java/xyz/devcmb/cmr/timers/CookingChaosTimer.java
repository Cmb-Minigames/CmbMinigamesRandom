package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CookingChaosController;
import xyz.devcmb.cmr.utils.Colors;

public class CookingChaosTimer extends TimerSuper implements Timer {
    public CookingChaosTimer() {
        super(10 * 60, 20, (early) -> {
            CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.endGame();
        });
    }
}
