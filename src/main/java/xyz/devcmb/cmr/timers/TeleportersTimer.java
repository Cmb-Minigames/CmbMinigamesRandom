package xyz.devcmb.cmr.timers;

import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.TeleportersController;

public class TeleportersTimer extends TimerSuper implements Timer {
    public TeleportersTimer() {
        super(-1, 20, (early) -> {
            TeleportersController controller = (TeleportersController) GameManager.getMinigameByName("Teleporters");
            if(controller == null) return;

            controller.endGame();
        });
    }
}
