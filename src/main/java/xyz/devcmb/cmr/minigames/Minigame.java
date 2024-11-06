package xyz.devcmb.cmr.minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Minigame {
    void start();
    void stop();
    void playerJoin(Player player);
    void playerLeave(Player player);
    default List<MinigameFlag> getFlags(){
        return new ArrayList<>();
    }
    void playerRespawn(PlayerRespawnEvent event);
    void playerDeath(PlayerDeathEvent event);
    void updateScoreboard(Player player);

    String getName();
    String getDescription();
}
