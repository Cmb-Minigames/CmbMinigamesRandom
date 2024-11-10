package xyz.devcmb.cmr.interfaces;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Utilities;

public class Stars {
    public static void showStarsActionBar(Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                String stars = Utilities.replaceNumbersWithLowOnes("⭐" + Database.getUserStars(player));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(stars));
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 40);
    }
}
