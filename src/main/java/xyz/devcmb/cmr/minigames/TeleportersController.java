package xyz.devcmb.cmr.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.minigames.bases.FFAMinigameBase;
import xyz.devcmb.cmr.minigames.teleporters.*;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

/**
 * The Teleporters minigame controller
 */
public class TeleportersController extends FFAMinigameBase implements Minigame {
    public Integer gameLives = 0;
    public Map<Player, Integer> playerLives = new HashMap<>();
    public Integer eventTimer = 30;
    private BukkitRunnable eventRunnable = null;
    public Boolean eventActive = false;
    private final List<TeleportersEvent> events = new ArrayList<>();
    private Boolean gameStarted = false;

    public Timer timer;

    public TeleportersController(){
        events.add(new FunnyStick(this));
        events.add(new Revival(this));
        events.add(new Restock(this));
        events.add(new Brewery(this));
    }

    @Override
    public void start() {
        gameStarted = false;

        Integer[] lives = {1, 5, 10};
        gameLives = lives[new Random().nextInt(lives.length)];

        super.start();

        players.forEach(player -> {
            player.teleport(spawnLocation);
            player.setSaturation(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
            playerLives.put(player, gameLives);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> new BukkitRunnable(){
            int cyles = 0;
            @Override
            public void run() {
                if(cyles == 5){
                    this.cancel();

                    Title selectedTitle = Title.title(
                        Component.text(gameLives.toString()).color(Colors.GOLD).decorate(TextDecoration.BOLD),
                        Component.text("Choosing the amount of lives").color(Colors.GREEN),
                        Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(40), Utilities.ticksToMilliseconds(20))
                    );

                    allPlayers.forEach(plr -> {
                        plr.showTitle(selectedTitle);
                        plr.playSound(plr.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                    });

                    return;
                }

                Title pickingTitle = Title.title(
                    Component.text(lives[new Random().nextInt(lives.length)].toString()),
                    Component.text("Choosing the amount of lives").color(Colors.GREEN),
                    Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(19), Utilities.ticksToMilliseconds(0))
                );

                allPlayers.forEach(plr -> {
                    plr.showTitle(pickingTitle);
                    plr.playSound(plr.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                });
                cyles++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20), 20 * 2);


        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            allPlayers.forEach(player -> Utilities.Countdown(player, 10));
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                gameStarted = true;
                Map<?, List<?>> kit = Kits.teleporters_kit;
                players.forEach(player -> {
                    Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                    player.setSaturation(0);
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
                });

                timer = TimerManager.runTimer("teleporters");

                eventRunnable = new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(eventActive) return;
                        eventTimer--;

                        if(eventTimer == 0){
                            eventActive = true;
                            TeleportersEvent event = Utilities.getRandom(events);

                            for(int i = 0; i < 4; i++){
                                TeleportersEvent randomEvent = Utilities.getRandom(events);

                                Title eventTitle = Title.title(
                                    Component.text(randomEvent.getName()).color(Colors.GOLD),
                                    Component.text(randomEvent.getDescription()),
                                    Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(19), Utilities.ticksToMilliseconds(0))
                                );

                                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () ->
                                    players.forEach(plr -> {
                                        plr.showTitle(eventTitle);
                                        plr.playSound(plr.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                                    }
                                ), 20 * i);
                            }

                            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {

                                Title eventTitle = Title.title(
                                    Component.text(event.getName()).color(Colors.GOLD).decorate(TextDecoration.BOLD),
                                    Component.text(event.getDescription()),
                                    Title.Times.times(Utilities.ticksToMilliseconds(0), Utilities.ticksToMilliseconds(40), Utilities.ticksToMilliseconds(10))
                                );

                                players.forEach(plr -> {
                                    plr.showTitle(eventTitle);
                                    plr.playSound(plr.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                                });
                                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), event::run, 20 * 3);
                            }, 20 * 4);
                        }
                    }
                };
                eventRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
            }, 10 * 20);
        }, 20 * 10);
    }

    @Override
    public void stop() {
        playerLives.clear();
        gameLives = 0;
        eventTimer = 60;
        eventActive = false;
        if(eventRunnable != null) eventRunnable.cancel();
        eventRunnable = null;
        gameStarted = false;
        timer = null;

        super.stop();
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.CANNOT_BREAK_BLOCKS,
            MinigameFlag.CANNOT_PLACE_BLOCKS,
            MinigameFlag.USE_CUSTOM_RESPAWN,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.DISABLE_BLOCK_DROPS
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        event.setRespawnLocation(spawnLocation);
        player.teleport(spawnLocation);

        if (!gameStarted) return;

        Integer lives = playerLives.get(player);
        if (lives == null || lives <= 0) {
            if (players.contains(player)) {
                players.remove(player);
                if (CmbMinigamesRandom.DeveloperMode && players.isEmpty()) {
                    players.add(event.getPlayer());
                }

                if (players.size() == 1) {
                    timer.end();
                    endGame();
                } else if (players.isEmpty()) {
                    stop();
                }
            }
            return;
        }

        if (!players.contains(player)) return;
        Kits.kitPlayer(Kits.teleporters_kit, player, Material.WHITE_CONCRETE);
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {
        if(playerLives.get(event.getEntity()) <= 0 || !gameStarted) return;
        playerLives.put(event.getEntity(), playerLives.get(event.getEntity()) - 1);
    }

    public void endGame(){
        timer = null;
        eventRunnable.cancel();
        eventRunnable = null;
        if(players.isEmpty()){
            stop();
        } else {
            super.endGame();
        }
    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("teleporters").getScoreboard(player)
        );
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
        return Map.of(
            StarSource.WIN, 30,
            StarSource.KILL, 2
        );
    }

    @Override
    public String getId() {
        return "teleporters";
    }

    @Override
    public String getName() {
        return "Teleporters";
    }

    @Override
    public String getDescription() {
        return "A minigame where you are given a stack of pearls and have to stay on the platform. At the start of the game, the amount of lives will be selected, which can either be 1, 5, 10, 15, or 20. Over time, items used to push other players off will be given through events that happen every 60 seconds. Last person standing wins!";
    }
}
