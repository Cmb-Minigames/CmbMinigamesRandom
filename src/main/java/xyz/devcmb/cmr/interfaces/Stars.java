package xyz.devcmb.cmr.interfaces;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Database;

/**
 * An interfaces class for showing stars in the action bar
 */
public class Stars {
    /**
     * Show the stars in the action bar
     * @param player The player to show the stars to
     */
    public static void showStarsActionBar(Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.hasPotionEffect(PotionEffectType.HUNGER)) return;
                Number userStars = Database.getUserStars(player);
                TextComponent stars = new TextComponent(" ".repeat(35 - userStars.toString().length()) + "⭐" + userStars);
                stars.setFont("cmbminigames:actionbar");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, stars);
            }
        }.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 40);
    }
}
