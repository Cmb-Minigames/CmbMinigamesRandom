package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.minigames.teleporters.*;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

public class TeleportersController implements Minigame {
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();
    public Integer gameLives = 0;
    public Map<Player, Integer> playerLives = new HashMap<>();
    public Integer eventTimer = 60;
    private BukkitRunnable eventRunnable = null;
    public Boolean eventActive = false;
    private final List<TeleportersEvent> events = new ArrayList<>();

    private Location spawnLocation = null;
    private Boolean gameStarted = false;

    public TeleportersController(){
        events.add(new FunnyStick(this));
        events.add(new Revival(this));
        events.add(new Restock(this));
        events.add(new Brewery(this));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        Utilities.gameStartReusable();
        gameStarted = false;

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        players.addAll(Bukkit.getOnlinePlayers());
        allPlayers.addAll(Bukkit.getOnlinePlayers());

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Integer[] lives = {1, 5, 10, 15, 20};
        gameLives = lives[new Random().nextInt(lives.length)];

        spawnLocation = Utilities.getLocationFromConfig(mapData, world, "spawn");

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
            allPlayers.forEach(plr -> Utilities.Countdown(plr, 10));
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
        players.clear();
        allPlayers.clear();
        playerLives.clear();
        gameLives = 0;
        spawnLocation = null;
        eventTimer = 60;
        eventActive = false;
        if(eventRunnable != null) eventRunnable.cancel();
        eventRunnable = null;
        gameStarted = false;

        Utilities.endGameResuable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> spawn = (Map<String, Object>) mapData.get("spawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) spawn.get("x")).doubleValue(), ((Number) spawn.get("y")).doubleValue(), ((Number) spawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Teleporters is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        players.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (players.isEmpty()) ? 0 : null;
        } else {
            if(players.size() == 1){
                Player winner = players.getFirst();
                Database.addUserStars(winner, getStarSources().get(StarSource.WIN).intValue());
                winner.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                winner.getInventory().clear();
                winner.setGameMode(GameMode.SPECTATOR);
            } else if(players.isEmpty()){
                return 0;
            }
        }

        return null;
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

        if(!gameStarted) return;
        if(playerLives.get(player) <= 0 && players.contains(player)){
            players.remove(player);
            if(CmbMinigamesRandom.DeveloperMode){
                if(players.isEmpty()){
                    players.add(event.getPlayer());
                }
            }

            if(players.size() == 1){
                endGame();
            } else if(players.isEmpty()){
                stop();
            }
        }

        if(!players.contains(player)) return;
        Kits.kitPlayer(Kits.teleporters_kit, player, Material.WHITE_CONCRETE);
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {
        if(playerLives.get(event.getEntity()) <= 0) return;
        playerLives.put(event.getEntity(), playerLives.get(event.getEntity()) - 1);
    }

    private void endGame(){
        eventRunnable.cancel();
        eventRunnable = null;
        if(players.size() == 1){
            GameManager.gameEnding = true;

            Player winner = players.getFirst();
            Database.addUserStars(winner, getStarSources().get(StarSource.WIN).intValue());
            winner.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
            winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            winner.getInventory().clear();
            winner.setGameMode(GameMode.SPECTATOR);

            allPlayers.forEach(player -> {
                if(player != winner){
                    player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SPECTATOR);
                }
            });

            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::stop, 20 * 8);
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
    public Map<StarSource, Number> getStarSources() {
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
