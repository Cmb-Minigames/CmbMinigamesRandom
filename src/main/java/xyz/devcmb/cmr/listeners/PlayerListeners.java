package xyz.devcmb.cmr.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.cosmetics.CosmeticInventory;
import xyz.devcmb.cmr.cosmetics.CosmeticManager;
import xyz.devcmb.cmr.interfaces.ActionBar;
import xyz.devcmb.cmr.interfaces.TabList;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class for player listeners
 */
public class PlayerListeners implements Listener {
    private final Map<Player, BukkitRunnable> countdowns = new HashMap<>();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String worldName = CmbMinigamesRandom.getPlugin().getConfig().getString("lobby.worldName");
        if(worldName == null || Bukkit.getWorld(worldName) == null){
            return;
        }

        Location spawnPoint = new Location(Bukkit.getWorld(worldName),
                CmbMinigamesRandom.getPlugin().getConfig().getDouble("lobby.spawn.x"),
                CmbMinigamesRandom.getPlugin().getConfig().getDouble("lobby.spawn.y"),
                CmbMinigamesRandom.getPlugin().getConfig().getDouble("lobby.spawn.z")
        );

        Player player = event.getPlayer();
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
        ActionBar.registerPlayer(player);
        CMScoreboardManager.initialize(player);
        GameManager.playerConnect(event);
        player.getInventory().clear();
        player.teleport(spawnPoint);
        CosmeticManager.playerJoin(player);
        CosmeticManager.equipHat(player);
        player.setRespawnLocation(spawnPoint);

        CosmeticInventory inventory = CosmeticManager.playerInventories.get(player);
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), inventory::giveInventoryItem, 2);

        if(!Database.userExists(player)){
            Database.createUser(player);
        }

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        PotionEffect hungerEffect = new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 255, true, false, false);
        player.addPotionEffect(hungerEffect);

        player.sendPlayerListHeader(
                Component.text(" ".repeat(5))
                        .append(Component.text("Cmb Minigames - Random").color(Colors.GOLD))
                        .append(Component.text(" ".repeat(5))));

        countdowns.put(player, new BukkitRunnable(){
            @Override
            public void run() {
                TabList.updateTabListName(player);
            }
        });
        countdowns.get(player).runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);

        GameManager.playerDisconnect(player);
        countdowns.get(player).cancel();
        ActionBar.unregisterPlayer(player);
    }
}
