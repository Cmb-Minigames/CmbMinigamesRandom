package xyz.devcmb.cmr.utils;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utilities {
    private static final Map<Character, String> lowNumbersMap = new HashMap<>();

    static {
        lowNumbersMap.put('0', "\uE00C");
        lowNumbersMap.put('1', "\uE003");
        lowNumbersMap.put('2', "\uE004");
        lowNumbersMap.put('3', "\uE005");
        lowNumbersMap.put('4', "\uE006");
        lowNumbersMap.put('5', "\uE007");
        lowNumbersMap.put('6', "\uE008");
        lowNumbersMap.put('7', "\uE009");
        lowNumbersMap.put('8', "\uE00A");
        lowNumbersMap.put('9', "\uE00B");
    }

    public static void Countdown(Player player, int totalSeconds){
        new BukkitRunnable(){
            int seconds = totalSeconds;
            @Override
            public void run() {
                if(seconds == 0){
                    this.cancel();
                    player.sendTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "GO!", "", 0, 40, 10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 2.5f);
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
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10, 1);
                seconds--;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    public static <T> T getRandom(List<T> list){
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static String replaceNumbersWithLowOnes(String stringWithNumbers) {
        StringBuilder result = new StringBuilder();
        for (char c : stringWithNumbers.toCharArray()) {
            if (lowNumbersMap.containsKey(c)) {
                result.append(lowNumbersMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
