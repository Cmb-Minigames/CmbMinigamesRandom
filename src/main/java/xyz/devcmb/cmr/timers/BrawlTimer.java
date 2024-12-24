package xyz.devcmb.cmr.timers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.BrawlController;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Utilities;

public class BrawlTimer extends TimerSuper implements Timer {
    public BrawlTimer() {
        super(-1, 20, (early) -> {
            BrawlController controller = (BrawlController) GameManager.getMinigameByName("Brawl");
            if(controller == null) return;
            if(early) Bukkit.broadcast(Component.text("The game has been ended early by an administrator").color(Colors.PURPLE));

            controller.allPlayers.forEach(player -> {
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);

                if(controller.players.contains(player)){
                    Title victoryTitle = Title.title(
                            Component.text("VICTORY").color(Colors.GOLD).decorate(TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
                    );
                    player.showTitle(victoryTitle);
                } else {
                    Title defeatTitle = Title.title(
                            Component.text("DEFEAT").color(Colors.RED).decorate(TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
                    );

                    player.showTitle(defeatTitle);
                }
            });

            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), controller::stop, 20 * 8);
        });
    }
}
