package xyz.devcmb.cmr;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.minigames.CaptureTheFlagController;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager {

    public static List<Minigame> minigames = new ArrayList<>();
    public static Minigame currentMinigame = null;
    public static boolean intermission = true;
    public static boolean intermissionCountdownInProgress = false;
    public static int timeLeft = 15;
    public static boolean pregame = false;
    public static boolean ingame = false;
    public static boolean playersFrozen = false;

    private static BukkitRunnable intermissionTimeDepreciation = null;

    public static void registerAllMinigames(){
        registerMinigame(new CaptureTheFlagController());
    }

    public static Minigame getMinigameByName(String name){
        for(Minigame minigame : minigames){
            if(minigame.getName().equalsIgnoreCase(name)){
                return minigame;
            }
        }
        return null;
    }

    public static void registerMinigame(Minigame minigame){
        minigames.add(minigame);
        CmbMinigamesRandom.LOGGER.info("Registered minigame: " + minigame.getName());
    }

    public static void playerConnect(Player player){
        if(ingame || pregame) {
            currentMinigame.playerJoin(player);
            return;
        }

        if((CmbMinigamesRandom.DeveloperMode ? (!Bukkit.getOnlinePlayers().isEmpty()) : (Bukkit.getOnlinePlayers().size() >= 2)) && !intermissionCountdownInProgress){
            intermissionCountdownInProgress = true;
            timeLeft = 15;

            intermissionTimeDepreciation = new BukkitRunnable() {
                @Override
                public void run() {
                    if(timeLeft == 0 || (CmbMinigamesRandom.DeveloperMode ? Bukkit.getOnlinePlayers().isEmpty() : Bukkit.getOnlinePlayers().size() < 2)){
                        this.cancel();
                        intermissionTimeDepreciation = null;
                        if(timeLeft == 0){
                            intermission = false;
                            pregame = true;
                            chooseRandom();
                        }
                    }

                    timeLeft -= 1;
                }
            };

            intermissionTimeDepreciation.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
        }
    }

    public static void chooseRandom() {
        Minigame minigame = Utilities.getRandom(minigames);

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown >= 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                        player.sendTitle(Utilities.getRandom(minigames).getName(), "", 0, 10, 0);
                    });
                    countdown--;
                } else {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                        player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + minigame.getName(), "", 0, 50, 15);
                    });
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Map<String, ?> map = MapLoader.loadRandomMap(minigame);
                            pregame = false;
                            ingame = true;
                            currentMinigame = minigame;
                            minigame.start(map);
                        }
                    }.runTaskLater(CmbMinigamesRandom.getPlugin(), 75);
                    this.cancel();
                }
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 10);
    }
}
