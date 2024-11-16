package xyz.devcmb.cmr.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import xyz.devcmb.cmr.CmbMinigamesRandom;

import java.util.HashMap;
import java.util.Map;

public class MusicBox {
    public static Map<String, Map<String, String>> tracks = new HashMap<>();

    public static void registerAllTracks(){
        registerTrack(
            "kaboomers",
            "minecraft:kaboomers",
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
            Utilities.showAdvancement(player, "minecraft:jukebox", "Now Playing", "\"" + track.get("song") + "\" by " + track.get("author"));
        });
    }
}
