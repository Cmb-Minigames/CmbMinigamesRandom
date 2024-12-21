package xyz.devcmb.cmr.utils.timers;

import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;

public class CTFTimer extends TimerSuper implements Timer {
    public CTFTimer() {
        super(60 * 15, 20, (early) -> {
            CaptureTheFlagController controller = (CaptureTheFlagController) GameManager.getMinigameByName("Capture The Flag");
            if(controller == null) return;

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
