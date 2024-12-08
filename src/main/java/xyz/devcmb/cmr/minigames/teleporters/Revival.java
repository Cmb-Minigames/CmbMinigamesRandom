package xyz.devcmb.cmr.minigames.teleporters;

import xyz.devcmb.cmr.minigames.TeleportersController;

/**
 * The Revival event
 */
public class Revival implements TeleportersEvent {
    public TeleportersController teleportersController;

    public Revival(TeleportersController controller){
        teleportersController = controller;
    }

    @Override
    public void run() {
        teleportersController.playerLives.forEach((player, lives) -> {
            if(lives < 1) return;
            teleportersController.playerLives.put(player, lives + 1);
        });

        teleportersController.eventTimer = 30;
        teleportersController.eventActive = false;
    }

    @Override
    public String getName() {
        return "Revival";
    }

    @Override
    public String getDescription() {
        return "Give all alive players 1 extra life";
    }
}
