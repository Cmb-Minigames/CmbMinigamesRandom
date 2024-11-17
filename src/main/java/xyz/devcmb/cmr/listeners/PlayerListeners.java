package xyz.devcmb.cmr.listeners;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.Stars;
import xyz.devcmb.cmr.interfaces.TabList;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;

import java.util.Objects;

public class PlayerListeners implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Location spawnPoint = new Location(Bukkit.getWorld("pregame"), -26.5, -42.5, -18.5);
        Player player = event.getPlayer();
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
        Stars.showStarsActionBar(player);
        CMScoreboardManager.initialize(player);
        GameManager.playerConnect(event);
        player.getInventory().clear();
        player.teleport(spawnPoint);
        player.setRespawnLocation(spawnPoint);

        if(!Database.userExists(player)){
            Database.createUser(player);
        }

        Audience audience = (Audience) player;
        audience.sendPlayerListHeader(Component.text(" ".repeat(5) + ChatColor.GOLD + ChatColor.BOLD + "Cmb Minigames - Random" + " ".repeat(5)));

        Bukkit.getScheduler().runTaskTimer(CmbMinigamesRandom.getPlugin(), () -> {
            TabList.updateTabListName(player);
        }, 0, 40);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        GameManager.playerDisconnect(event.getPlayer());
    }
}
