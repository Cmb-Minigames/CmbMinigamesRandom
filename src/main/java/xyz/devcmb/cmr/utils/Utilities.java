package xyz.devcmb.cmr.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.List;
import java.util.Random;

public class Utilities {
    public static void Countdown(Player player, int totalSeconds){
        new BukkitRunnable(){
            int seconds = totalSeconds;
            @Override
            public void run() {
                if(seconds == 0){
                    this.cancel();
                    player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", "", 0, 40, 10);
                    return;
                }

                ChatColor color = ChatColor.WHITE;

                switch(seconds){
                    case 3:
                        color = ChatColor.GREEN;
                        break;
                    case 2:
                        color = ChatColor.YELLOW;
                        break;
                    case 1:
                        color = ChatColor.RED;
                        break;
                    default:
                        break;
                }

                player.sendTitle(color.toString() + ChatColor.BOLD + "> " + seconds + " <", "The game will begin shortly", 0, 20, 0);
                seconds--;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    public static <T> T getRandom(List<T> list){
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

}
