package xyz.devcmb.cmr.minigames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaceMurderers implements Minigame {
    public List<Player> players = new ArrayList<>();
    public Player hunter = null;
    @Override
    public void start() {
        players.addAll(Bukkit.getOnlinePlayers());
        hunter = Utilities.getRandom(players);
    }

    @Override
    public void stop() {
        hunter = null;
        players.clear();
        Utilities.endGameResuable();
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
        return List.of(
            MinigameFlag.CANNOT_PLACE_BLOCKS,
            MinigameFlag.DISABLE_FALL_DAMAGE
        );
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
        return "macemurderers";
    }

    @Override
    public String getName() {
        return "Mace Murderers";
    }

    @Override
    public String getDescription() {
        return "Collect materials around the map to craft weapons to fight an assasin with a mace and wind charges. The assasin will win if they hunt all of their targets, and the players will win if they can kill the assasin.";
    }
}
