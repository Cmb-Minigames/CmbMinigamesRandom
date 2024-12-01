package xyz.devcmb.cmr.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for playing music in the server
 */
public class MusicBox {
    public static Map<String, Map<String, String>> tracks = new HashMap<>();

    /**
     * Register all the tracks
     */
    public static void registerAllTracks(){
        registerTrack(
            "kaboomers",
            "cmbminigames:kaboomers",
            "Kaboom!",
            "Nibbl_z"
        );
    }


    private static void registerTrack(String name, String musicPath, String songName, String author){
        tracks.put(name, Map.of(
            "path", musicPath,
            "song", songName,
            "author", author
        ));
    }

    public static void playTrack(String music) {
        Map<String, String> track = tracks.get(music);
        if(track == null) {
            CmbMinigamesRandom.LOGGER.warning("Failed to find a track for " + music);
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player, track.get("path"), SoundCategory.MUSIC, 1, 1);
//            Utilities.showAdvancement(player, "minecraft:jukebox", "Now Playing", "\"" + track.get("song") + "\" by " + track.get("author"));
            Audience audience = CmbMinigamesRandom.adventure().player(player);
            final BossBar bossBar = BossBar.bossBar(
                    Component.text("\"" + track.get("song") + "\" by " + track.get("author")),
                    0,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
            );

            audience.showBossBar(bossBar);
            new BukkitRunnable() {
                double progress = 0;
                final double increment = 1.0 / 100;

                @Override
                public void run() {
                    if (progress >= 1) {
                        audience.hideBossBar(bossBar);
                        this.cancel();
                    } else {
                        progress += increment;
                        bossBar.progress((float) progress);
                    }
                }
            }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 1);
        });
    }

}
