package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.ElectricEelController;
import xyz.devcmb.cmr.utils.Colors;

public class ElectricEelTimer extends TimerSuper implements Timer {
    public ElectricEelTimer() {
        super(60 * 4, 20, (early) -> {
            ElectricEelController controller = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.endGame();
        });
    }
}
