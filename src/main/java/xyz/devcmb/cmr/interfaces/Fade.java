package xyz.devcmb.cmr.interfaces;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.time.Duration;

/**
 * An interfaces class to show a fading transition
 */
public class Fade {
    /**
     * Fades the player
     * @param player The player to fade
     * @param up The time to fade up
     * @param stay The time to stay
     * @param down The time to fade down
     */
    public static void fadePlayer(Player player, Integer up, Integer stay, Integer down){
        CmbMinigamesRandom.LOGGER.info("Fading player " + player.getName() + " with up: " + up + ", stay: " + stay + ", down: " + down);

        Component text = Component.text("\uE01F")
                        .font(Key.key("cmbminigames:fade"));

        Title title = Title.title(
                text,
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(up * 50),
                        Duration.ofMillis(stay * 50),
                        Duration.ofMillis(down * 50)
                )
        );

        player.showTitle(title);
    }
}
