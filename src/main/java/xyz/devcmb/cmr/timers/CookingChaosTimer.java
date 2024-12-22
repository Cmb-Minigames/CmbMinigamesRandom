package xyz.devcmb.cmr.timers;

import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CookingChaosController;

public class CookingChaosTimer extends TimerSuper implements Timer {
    public CookingChaosTimer() {
        super(10 * 60, 20, (early) -> {
            CookingChaosController controller = (CookingChaosController) GameManager.getMinigameByName("Cooking Chaos");
            if(controller == null) return;

            controller.endGame();
        });
    }
}
