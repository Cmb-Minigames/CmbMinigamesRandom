package xyz.devcmb.cmr.interfaces;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

public class Stars {
    public static void showStarsActionBar(Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("⭐ 0"));
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 40);
    }
}
