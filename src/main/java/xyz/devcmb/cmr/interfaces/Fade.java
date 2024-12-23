package xyz.devcmb.cmr.interfaces;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.time.Duration;

public class Fade {
    public static void fadePlayer(Player player, Integer up, Integer stay, Integer down){
        CmbMinigamesRandom.LOGGER.info("Fading player " + player.getName() + " with up: " + up + ", stay: " + stay + ", down: " + down);

        Component text = Component.text("\uE01F")
                .style(Style.style()
                        .font(Key.key("cmbminigames:fade"))
                        .build());

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
