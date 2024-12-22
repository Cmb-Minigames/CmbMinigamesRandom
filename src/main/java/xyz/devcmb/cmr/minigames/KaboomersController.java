package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.minigames.bases.Teams2MinigameBase;
import xyz.devcmb.cmr.utils.*;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;

import java.util.*;

/**
 * The Kaboomers minigame controller
 */
public class KaboomersController extends Teams2MinigameBase implements Minigame {
    public List<Block> redBlocks = new ArrayList<>();
    public List<Block> blueBlocks = new ArrayList<>();
    public Timer timer;

    @Override
    public void start() {
        super.start();
        RED.forEach(player -> {
            assert redSpawn != null;
            player.teleport(Utilities.findValidLocation(redSpawn));
            Fade.fadePlayer(player, 0, 0, 40);
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
        });

        BLUE.forEach(player -> {
            assert blueSpawn != null;
            player.teleport(Utilities.findValidLocation(blueSpawn));
            Fade.fadePlayer(player, 0, 0, 40);
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
        });

        GameManager.playersFrozen = true;
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());

        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setSaturation(0f);
                    Utilities.Countdown(player, 10);
                });

                Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                    MusicBox.playTrack("kaboomers");
                    RED.forEach(player -> {
                        Map<?, List<?>> kit = Kits.kaboomers_kit;
                        Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
                    });

                    BLUE.forEach(player -> {
                        Map<?, List<?>> kit = Kits.kaboomers_kit;
                        Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
                    });

                    GameManager.playersFrozen = false;
                    GameManager.ingame = true;

                    timer = TimerManager.runTimer("kaboomers");
                }, 20 * 10);
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 2);
    }

    @Override
    public void stop() {
        super.stop();
        timer = null;
        redBlocks.clear();
        blueBlocks.clear();
    }

    public void endGame(){
        GameManager.gameEnding = true;
        timer = null;

        if(redBlocks.size() > blueBlocks.size()){
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(blueBlocks.size() > redBlocks.size()){
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 7);
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_BLOCK_DROPS,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.DO_NOT_CONSUME_FIREWORKS,
            MinigameFlag.USE_CUSTOM_RESPAWN
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.kaboomers_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(redSpawn);
            player.teleport(redSpawn);
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.kaboomers_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(blueSpawn);
            player.teleport(blueSpawn);
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("kaboomers").getScoreboard(player)
        );
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
        return Map.of(
            StarSource.KILL, 2,
            StarSource.WIN, 15
        );
    }

    @Override
    public String getId() {
        return "kaboomers";
    }

    @Override
    public String getName() {
        return "Kaboomers";
    }

    @Override
    public String getDescription() {
        return "Claim area around the map as your own by shooting a fireball at it, which will claim up to a 3 by 3 by 3 cube, whichever team has the most claimed by the end wins";
    }
}
