package xyz.devcmb.cmr.minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Minigame {
    /**
     * Called once the random selection of the minigame is done
     */
    void start();
    /**
     * Called as a result of playerLeave or called internally for cleanup purposes
     */
    void stop();
    /**
     * Called when a player joins the game
     * @param event The PlayerJoinEvent from the event listener in PlayerListeners
     */
    void playerJoin(PlayerJoinEvent event);
    /**
     * Called when a player leaves the game
     * @param player The player that left
     * @return How long, if at all, until the game will call the stop method
     */
    Number playerLeave(Player player);
    /**
     * Called to receive a list of Minigame Feature Flags associated with the event listeners in MinigameListeners
     * @return A list of all the MinigameFlag enums
     */
    default List<MinigameFlag> getFlags(){
        return new ArrayList<>();
    }
    /**
     * Called when a player respawns
     * @param event The PlayerRespawnEvent from the event listener in MinigameListeners
     */
    void playerRespawn(PlayerRespawnEvent event);
    /**
     * Called when a player dies
     * @param event The PlayerDeathEvent from the event listener in MinigameListeners
     */
    void playerDeath(PlayerDeathEvent event);
    /**
     * Called to update the scoreboard for a player
     * @param player The player to update the scoreboard for
     */
    void updateScoreboard(Player player);
    /**
     * Called to get a map of all the star sources and how many stars they give, used internally for objective and win, and handled externally for kills
     * @return A map of all the star sources and how many stars they give
     */
    Map<StarSource, Number> getStarSources();

    // All the information about the minigame
    String getId();
    String getName();
    String getDescription();
}
