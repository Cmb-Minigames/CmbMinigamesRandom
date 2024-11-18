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
import xyz.devcmb.cmr.utils.Utilities;
import java.util.*;

public class SimonSaysController implements Minigame {
    public Player simon = null;
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();
    public boolean pvp = false;

    private Location spawnLocation;
    private Location simonLocation;

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        Map<String, Object> spawn = (Map<String, Object>) mapData.get("spawn");
        Map<String, Object> simonSpawn = (Map<String, Object>) mapData.get("simonSpawn");

        if (spawn == null || simonSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        players.addAll(Bukkit.getOnlinePlayers());
        allPlayers.addAll(Bukkit.getOnlinePlayers());

        String worldName = (String) mapData.get("worldName");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        spawnLocation = new Location(
                world,
                ((Number) spawn.get("x")).doubleValue(),
                ((Number) spawn.get("y")).doubleValue(),
                ((Number) spawn.get("z")).doubleValue()
        );

        simonLocation = new Location(
                world,
                ((Number) simonSpawn.get("x")).doubleValue(),
                ((Number) simonSpawn.get("y")).doubleValue(),
                ((Number) simonSpawn.get("z")).doubleValue()
        );

        players.forEach(player -> {
            player.teleport(spawnLocation);
            player.setSaturation(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            Utilities.Countdown(player, 10);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::decideSimon, 13 * 10);
    }

    private void decideSimon(){
        pickSimon();
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            simon.teleport(simonLocation);
        }, 15 * 5);
    }

    private void pickSimon(){
        simon = players.get(new Random().nextInt(players.size()));

        new BukkitRunnable(){
            int done = 0;
            @Override
            public void run() {
               if(done == 3){
                   allPlayers.forEach(player -> {
                       player.sendTitle(ChatColor.AQUA + "The next simon will be", ChatColor.GOLD + simon.getName(), 0, 50, 20);
                       player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                   });
                   this.cancel();
                   return;
               }

               allPlayers.forEach(player -> {
                   player.sendTitle(ChatColor.AQUA + "The next simon will be", players.get(new Random().nextInt(players.size())).getName(), 0, 16, 0);
                   player.playSound(player.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE, 1, 1);
               });

               done++;
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 15);
    }

    @Override
    public void stop() {

    }

    @Override
    public void playerJoin(PlayerJoinEvent event) {

    }

    @Override
    public Number playerLeave(Player player) {
        return null;
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return Minigame.super.getFlags();
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {

    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {

    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of();
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
