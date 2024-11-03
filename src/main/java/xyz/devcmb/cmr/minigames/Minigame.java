package xyz.devcmb.cmr.minigames;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Minigame {
    void start(Map<String, ?> map);
    void stop();
    void playerJoin(Player player);
    void playerLeave(Player player);
    default List<MinigameFlag> getFlags(){
        return new ArrayList<>();
    }
    void playerRespawn(Player player);
    void updateScoreboard(Player player);

    String getName();
    String getDescription();
}
