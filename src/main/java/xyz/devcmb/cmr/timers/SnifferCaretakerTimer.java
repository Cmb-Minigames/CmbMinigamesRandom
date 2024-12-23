package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.SnifferCaretakerController;
import xyz.devcmb.cmr.utils.Colors;

public class SnifferCaretakerTimer extends TimerSuper implements Timer {
    public SnifferCaretakerTimer() {
        super(-1, 20, (early) -> {
            SnifferCaretakerController controller = (SnifferCaretakerController) GameManager.getMinigameByName("Sniffer Caretaker");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.endGame();
        });
    }
}
