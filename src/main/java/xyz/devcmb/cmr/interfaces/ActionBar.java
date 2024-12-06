package xyz.devcmb.cmr.interfaces;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Database;

import java.util.HashMap;
import java.util.Map;

/**
 * An interfaces class for showing elements in the Action Bar
 */
public class ActionBar {
    public static Map<Player, Integer> userStars = new HashMap<>();
    public static final Map<Player, BukkitRunnable> sendActionBarTasks = new HashMap<>();
    /**
     * Send the action bar to the player
     * @param player The player to show the action bar to
     */
    private static void sendActionBar(Player player){
        if(!player.hasPotionEffect(PotionEffectType.HUNGER)){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            return;
        }

        TextComponent stars = new TextComponent(" ".repeat(30 + userStars.get(player).toString().length()) + "\uE000 " + userStars.get(player));
        stars.setFont("cmbminigames:actionbar");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, stars);
    }

    /**
     * Register a player to show the action bar for
     * @param player The player to register
     */
    public static void registerPlayer(Player player){
        userStars.put(player, Database.getUserStars(player));

        sendActionBarTasks.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                sendActionBar(player);
            }
        });
        sendActionBarTasks.get(player).runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 5);

        // Resync every minute
        Bukkit.getScheduler().runTaskTimer(CmbMinigamesRandom.getPlugin(), () -> userStars.put(player, Database.getUserStars(player)), 0, 60 * 20);
    }

    /**
     * Unregister a player from showing the action bar
     * @param player The player to unregister
     */
    public static void unregisterPlayer(Player player){
        sendActionBarTasks.get(player).cancel();
        sendActionBarTasks.remove(player);
        userStars.remove(player);
    }
}
