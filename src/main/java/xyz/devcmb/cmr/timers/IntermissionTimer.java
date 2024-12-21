package xyz.devcmb.cmr.timers;

import xyz.devcmb.cmr.GameManager;

public class IntermissionTimer extends TimerSuper implements Timer {
    public IntermissionTimer() {
        super(30, 20, (early) -> {
            GameManager.intermission = false;
            GameManager.pregame = true;
            GameManager.chooseRandom();
        });
    }
}
