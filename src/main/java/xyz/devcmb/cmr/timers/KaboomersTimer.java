package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.KaboomersController;
import xyz.devcmb.cmr.utils.Colors;

public class KaboomersTimer extends TimerSuper implements Timer {
    public KaboomersTimer() {
        super(120, 20, (early) -> {
            KaboomersController controller = (KaboomersController) GameManager.getMinigameByName("Kaboomers");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.endGame();
        });
    }
}
