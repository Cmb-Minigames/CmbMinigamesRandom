package xyz.devcmb.cmr.interfaces;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Colors;
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
    private static void sendActionBar(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
            player.sendActionBar(Component.empty());
            return;
        }

        String starsText = String.format("%010d", userStars.get(player));
        Component stars = Component.text(" ".repeat(42))
                .color(TextColor.fromHexString("#4e5c24"))
                .append(Component.text("\uE000 "));

        boolean leading = true;
        for (char c : starsText.toCharArray()) {
            TextColor color;
            if (leading && c == '0') {
                color = Colors.DARK_GRAY;
            } else {
                color = Colors.WHITE;
                leading = false;
            }
            stars = stars.append(Component.text(c).color(color));
        }

        stars = stars.font(Key.key("cmbminigames", "actionbar"));
        player.sendActionBar(stars);
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
