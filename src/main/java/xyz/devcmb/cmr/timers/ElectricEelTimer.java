package xyz.devcmb.cmr.timers;

import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.ElectricEelController;

public class ElectricEelTimer extends TimerSuper implements Timer {
    public ElectricEelTimer() {
        super(60 * 4, 20, (early) -> {
            ElectricEelController controller = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
            if(controller == null) return;

            controller.endGame();
        });
    }
}
