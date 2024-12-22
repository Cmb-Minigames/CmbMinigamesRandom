package xyz.devcmb.cmr.timers;

import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;

public class SnifferCaretakerTimer extends TimerSuper implements Timer {
    public SnifferCaretakerTimer() {
        super(-1, 20, (early) -> {
            SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
            if(controller == null) return;
            controller.endGame();
        });
    }
}
