package xyz.devcmb.cmr;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.minigames.*;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

public class GameManager {

    public static List<Minigame> minigames = new ArrayList<>();
    public static Minigame currentMinigame = null;
    public static boolean intermission = true;
    public static boolean intermissionCountdownInProgress = false;
    public static int timeLeft = 30;
    public static boolean pregame = false;
    public static boolean ingame = false;
    public static boolean playersFrozen = false;
    public static Map<String, ?> currentMap = null;
    public static boolean gameEnding = false;
    public static BukkitRunnable intermisionRunnable = null;
    public static boolean paused = false;
    public static Map<Player, Number> kills = new HashMap<>();
    public static Map<Minigame, Number> minigamePlays = new HashMap<>();
    public static Minigame selectedMinigame = null;
    public static Map<Player, ChatColor> teamColors = new HashMap<>();
    private static BukkitRunnable intermissionTimeDepreciation = null;

    public static void registerAllMinigames(){
        registerMinigame(new CaptureTheFlagController());
        registerMinigame(new KaboomersController());
        registerMinigame(new BrawlController());
        registerMinigame(new SnifferCaretakerController());
        registerMinigame(new CookingChaosController());
    }

    public static Minigame getMinigameByName(String name){
        for(Minigame minigame : minigames){
            if(minigame.getName().equalsIgnoreCase(name)){
                return minigame;
            }
        }

        return null;
    }

    public static Minigame getMinigameById(String id){
        for(Minigame minigame : minigames){
            if(minigame.getId().equalsIgnoreCase(id)){
                return minigame;
            }
        }
        return null;
    }

    public static void registerMinigame(Minigame minigame){
        minigames.add(minigame);
        minigamePlays.put(minigame, 0);
        CmbMinigamesRandom.LOGGER.info("Registered minigame: " + minigame.getName());
    }

    public static void playerConnect(PlayerJoinEvent event){
        kills.put(event.getPlayer(), 0);
        teamColors.put(event.getPlayer(), ChatColor.WHITE);
        if(ingame || pregame) {
            currentMinigame.playerJoin(event);
        } else if(!paused && intermissionTimeDepreciation == null && (CmbMinigamesRandom.DeveloperMode ? !Bukkit.getOnlinePlayers().isEmpty() : Bukkit.getOnlinePlayers().size() >= 2)){
            prepare();
        }
    }

    public static void playerDisconnect(Player player){
        kills.remove(player);
        teamColors.remove(player);
        if(ingame || pregame){
            Number endTimer = currentMinigame.playerLeave(player);
            if(endTimer == null) return;

            new BukkitRunnable(){
                @Override
                public void run() {
                    currentMinigame.stop();
                }
            }.runTaskLater(CmbMinigamesRandom.getPlugin(), endTimer.intValue() * 20L);
        }
    }

    public static void prepare(){
        pregame = false;
        ingame = false;
        gameEnding = false;
        intermissionTimeDepreciation = null;
        playersFrozen = false;
        currentMinigame = null;
        intermission = true;
        intermissionCountdownInProgress = false;
        timeLeft = 30;
        kills.replaceAll((player, kills) -> 0);
        cleanup();

        startIntermissionRunnable();
    }

    private static void startIntermissionRunnable() {
        if(intermisionRunnable != null) {
            intermisionRunnable.cancel();
            intermisionRunnable = null;
        }

        intermisionRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(paused) return;
                if((CmbMinigamesRandom.DeveloperMode ? (!Bukkit.getOnlinePlayers().isEmpty()) : (Bukkit.getOnlinePlayers().size() >= 2)) && !intermissionCountdownInProgress){
                    doIntermission();
                    this.cancel();
                    intermisionRunnable = null;
                }
            }
        };

        intermisionRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    public static void doIntermission(){
        if(intermissionTimeDepreciation != null) return;
        intermissionTimeDepreciation = new BukkitRunnable() {
            @Override
            public void run() {
                if(paused){
                    this.cancel();
                    intermission = false;
                    intermissionTimeDepreciation = null;
                    startIntermissionRunnable();
                    return;
                } else if(!intermission) {
                    intermission = true;
                }

                if(timeLeft <= 0 || (CmbMinigamesRandom.DeveloperMode ? Bukkit.getOnlinePlayers().isEmpty() : Bukkit.getOnlinePlayers().size() < 2)){
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

    public static void chooseRandom() {
        Minigame minigame = selectedMinigame != null ? selectedMinigame : Utilities.getRandom(minigames);
        selectedMinigame = null;
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
                            currentMap = MapLoader.loadRandomMap(minigame);
                            pregame = false;
                            ingame = true;
                            currentMinigame = minigame;

                            minigamePlays.put(minigame, minigamePlays.get(minigame).intValue() + 1);
                            minigame.start();
                        }
                    }.runTaskLater(CmbMinigamesRandom.getPlugin(), 75);
                    this.cancel();
                }
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 10);
    }

    public static void cleanup(){
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
            player.getInventory().clear();
            player.setGlowing(false);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }), 20);
    }
}
