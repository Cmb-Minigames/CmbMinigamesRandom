package xyz.devcmb.cmr.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for playing music in the server
 */
public class MusicBox {
    public static final Map<String, Map<String, Object>> tracks = new HashMap<>();
    private static final Map<Player, BukkitRunnable> runnables = new HashMap<>();

    /**
     * Register all the tracks
     */
    public static void registerAllTracks(){
        registerTrack(
            "kaboomers",
            "cmbminigames:kaboomers",
            "Kaboom!",
            "Nibbl_z",
            128,
            false
        );
    }

    /**
     * Register a track
     * @param name The name of the track
     * @param musicPath The path to the music
     * @param songName The name of the song
     * @param author The author of the song
     */
    private static void registerTrack(String name, String musicPath, String songName, String author, Integer time, Boolean loop){
        tracks.put(name, Map.of(
            "path", musicPath,
            "song", songName,
            "author", author,
            "time", time,
            "loop", loop
        ));
    }

    /**
     * Play a track
     * @param music The name of the track
     */
    public static void playTrack(String music) {
        Map<String, Object> track = tracks.get(music);
        if(track == null) {
            CmbMinigamesRandom.LOGGER.warning("Failed to find a track for " + music);
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player, (String) track.get("path"), SoundCategory.MUSIC, 1, 1);

            if((Boolean) track.get("loop")){
                loopTrack(player, music);
            }

            final BossBar bossBar = BossBar.bossBar(
                    Component.text("\"" + track.get("song") + "\" by " + track.get("author")),
                    0,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
            );

            player.showBossBar(bossBar);
            new BukkitRunnable() {
                double progress = 0;
                final double increment = 1.0 / 100;

                @Override
                public void run() {
                    if (progress >= 1) {
                        player.hideBossBar(bossBar);
                        this.cancel();
                    } else {
                        progress += increment;
                        bossBar.progress((float) progress);
                    }
                }
            }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 1);
        });
    }

    static void loopTrack(Player player, String music) {
        Map<String, Object> track = tracks.get(music);

        if(runnables.get(player) != null) {
            runnables.get(player).cancel();
            runnables.put(player, null);
        }

        runnables.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player, (String) track.get("path"), SoundCategory.MUSIC, 1, 1);
                loopTrack(player, music);
            }
        });
        runnables.get(player).runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, (Integer) track.get("time") * 20);
    }

    public static void stopTrack(Player player) {
        if(runnables.containsKey(player)) {
            runnables.get(player).cancel();
            player.stopAllSounds();
            runnables.remove(player);
        }
    }
}
