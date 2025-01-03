package xyz.devcmb.cmr;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.minigames.*;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;

import java.util.*;

/**
 * Manages the state of the loop and the minigames attached to it
 */
public class GameManager {
    public static List<Minigame> minigames = new ArrayList<>();
    public static Minigame currentMinigame = null;
    public static boolean intermission = true;
    public static boolean pregame = false;
    public static boolean ingame = false;
    public static boolean playersFrozen = false;
    public static Map<String, ?> currentMap = null;
    public static boolean gameEnding = false;
    public static BukkitRunnable intermisionRunnable = null;
    public static Map<Player, Number> kills = new HashMap<>();
    public static Map<Minigame, Number> minigamePlays = new HashMap<>();
    public static Minigame selectedMinigame = null;
    public static Map<Player, NamedTextColor> teamColors = new HashMap<>();
    public static Timer intermissionTimer = null;

    /**
     * Register all the minigames
     */
    public static void registerAllMinigames(){
        registerMinigame(new CaptureTheFlagController());
        registerMinigame(new KaboomersController());
        registerMinigame(new BrawlController());
        registerMinigame(new SnifferCaretakerController());
        registerMinigame(new CookingChaosController());
        registerMinigame(new ElectricEelController());
        registerMinigame(new TeleportersController());
    }

    /**
     * Get a minigme by its name
     * @param name The name of the minigame
     * @return The minigame controller
     */
    public static Minigame getMinigameByName(String name){
        for(Minigame minigame : minigames){
            if(minigame.getName().equalsIgnoreCase(name)){
                return minigame;
            }
        }

        return null;
    }

    /**
     * Get a minigame by its id
     * @param id The id of the minigame
     * @return The minigame controller
     */
    public static Minigame getMinigameById(String id){
        for(Minigame minigame : minigames){
            if(minigame.getId().equalsIgnoreCase(id)){
                return minigame;
            }
        }
        return null;
    }

    /**
     * Register a single minigame
     * @param minigame The controller of the minigame
     */
    public static void registerMinigame(Minigame minigame){
        minigames.add(minigame);
        minigamePlays.put(minigame, 0);
        CmbMinigamesRandom.LOGGER.info("Registered minigame: " + minigame.getName());
    }

    /**
     * Invoke events when a player connects to the server
     * @param event The join event
     */
    public static void playerConnect(PlayerJoinEvent event){
        kills.put(event.getPlayer(), 0);
        teamColors.put(event.getPlayer(), NamedTextColor.WHITE);
        if(ingame || pregame) {
            currentMinigame.playerJoin(event);
        } else if(intermissionTimer == null && (CmbMinigamesRandom.DeveloperMode ? !Bukkit.getOnlinePlayers().isEmpty() : Bukkit.getOnlinePlayers().size() >= 2)){
            prepare();
        }
    }

    /**
     * Invoke events when a player disconnects from the server
     * @param player The player that disconnected
     */
    public static void playerDisconnect(Player player){
        kills.remove(player);
        teamColors.remove(player);
        if((ingame || pregame) && !gameEnding){
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

    /**
     * Prepare the game for the next round
     */
    public static void prepare(){
        pregame = false;
        ingame = false;
        gameEnding = false;
        intermissionTimer = null;
        playersFrozen = false;
        currentMinigame = null;
        intermission = true;
        kills.replaceAll((player, kills) -> 0);
        TimerManager.clearTimers();

        startIntermissionRunnable();
    }

    /**
     * Determines when to start intermission
     */
    private static void startIntermissionRunnable() {
        if(intermisionRunnable != null) {
            intermisionRunnable.cancel();
            intermisionRunnable = null;
        }

        intermisionRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(TimerManager.paused) return;
                if((CmbMinigamesRandom.DeveloperMode ? (!Bukkit.getOnlinePlayers().isEmpty()) : (Bukkit.getOnlinePlayers().size() >= 2)) && intermissionTimer == null){
                    doIntermission();
                    this.cancel();
                    intermisionRunnable = null;
                }
            }
        };

        intermisionRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }


    /**
     * Start the intermission timer
     * See {@link TimerManager#runTimer(String)}
     */
    public static void doIntermission(){
        if(intermissionTimer != null) return;
        intermissionTimer = TimerManager.runTimer("intermission");
    }

    /**
     * Do the picker for a random minigame
     */
    public static void chooseRandom() {
        Minigame minigame = selectedMinigame != null ? selectedMinigame : Utilities.getRandom(minigames);
        selectedMinigame = null;
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown >= 0) {
                    Title selectTitle = Title.title(
                            Component.text(Utilities.getRandom(minigames).getName()),
                            Component.empty(),
                            Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(10), Utilities.ticksToMilliseconds(0))
                    );

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                        player.showTitle(selectTitle);
                    });
                    countdown--;
                } else {
                    Title finalSelectTitle = Title.title(
                            Component.text(minigame.getName()).color(Colors.GOLD).decorate(TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(10), Utilities.ticksToMilliseconds(0))
                    );

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                        player.showTitle(finalSelectTitle);
                    });
                    currentMap = MapLoader.loadRandomMap(minigame);

                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            Bukkit.getOnlinePlayers().forEach(player -> {
                               player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 50, 4));
                               Fade.fadePlayer(player, 50, 20, 30);
                            });
                            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                                pregame = false;
                                ingame = true;
                                currentMinigame = minigame;

                                minigamePlays.put(minigame, minigamePlays.get(minigame).intValue() + 1);
                                minigame.start();
                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                                    player.setHealth(20);
                                    player.setSaturation(0);
                                    player.setFoodLevel(20);
                                });
                            }, 50);
                        }
                    }.runTaskLater(CmbMinigamesRandom.getPlugin(), 45);
                    this.cancel();
                }
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 10);
    }

    /**
     * Cleanup the last minigame
     * @deprecated Use {@link Utilities#endGameResuable()} in the stop function of your minigame instead.
     */
    @Deprecated
    public static void cleanup(){
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
            player.setGlowing(false);
        }), 20);
    }
}
