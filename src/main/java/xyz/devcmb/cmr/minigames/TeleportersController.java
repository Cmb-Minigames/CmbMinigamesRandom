package xyz.devcmb.cmr.minigames;

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
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

public class TeleportersController extends FFAMinigameBase implements Minigame {
    public Integer gameLives = 0;
    public Map<Player, Integer> playerLives = new HashMap<>();
    public Integer eventTimer = 30;
    private BukkitRunnable eventRunnable = null;
    public Boolean eventActive = false;
    private final List<TeleportersEvent> events = new ArrayList<>();

    private Boolean gameStarted = false;

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
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            playerLives.put(player, gameLives);
        });

        new BukkitRunnable(){
            int cyles = 0;
            @Override
            public void run() {
                if(cyles == 5){
                    this.cancel();
                    allPlayers.forEach(plr -> {
                        plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + gameLives.toString(), ChatColor.GREEN + "Choosing the amount of lives", 0, 40, 20);
                        plr.playSound(plr.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                    });
                    return;
                }

                allPlayers.forEach(plr -> {
                    plr.sendTitle(lives[new Random().nextInt(lives.length)].toString(), ChatColor.GREEN + "Choosing the amount of lives", 0, 19, 0);
                    plr.playSound(plr.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                });
                cyles++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            allPlayers.forEach(player -> Utilities.Countdown(player, 10));
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                gameStarted = true;
                Map<?, List<?>> kit = Kits.teleporters_kit;
                players.forEach(player -> {
                    Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                    player.setSaturation(0);
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
                });

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
                                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () ->
                                    players.forEach(plr -> {
                                        plr.sendTitle(ChatColor.GOLD + randomEvent.getName(), randomEvent.getDescription(), 0, 19, 0);
                                        plr.playSound(plr.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
                                    }
                                ), 20 * i);
                            }

                            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                                players.forEach(plr -> {
                                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + event.getName(), event.getDescription(), 0, 40, 10);
                                    plr.playSound(plr.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                                });
                                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), event::run, 20 * 3);
                            }, 20 * 4);
                        }
                    }
                };
                eventRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
            }, 10 * 20);
        }, 20 * 8);

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

    protected void endGame(){
        eventRunnable.cancel();
        eventRunnable = null;
        if(players.size() == 1){
            super.endGame();
        } else if(players.isEmpty()){
            stop();
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
