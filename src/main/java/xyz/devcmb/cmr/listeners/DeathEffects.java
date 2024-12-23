package xyz.devcmb.cmr.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.minigames.MinigameFlag;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Format;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.List;

/**
 * A class for death effects
 */
public class DeathEffects implements Listener {
    private static final List<String> randomizedDeathMessages = List.of(
            "{player} tripped",
            "{player} fell and couldn't get up",
            "{player} forgot this wasn't roblox",
            "{player} didn't try hard enough",
            "{player}'s dad wasn't proud",
            "{player} thought they could speed bridge",
            "{player} got roasted on the internet",
            "Cmb personally doesn't like {player}",
            "{player} had a skill issue",
            "{player} became a pork chop",
            "{player} went kaboom",
            "{player} was caught playing fortnite",
            "{player}'s brother did not tell them how to chase the bag",
            "{player} should get their eyes checked",
            "{player} should buy a better pc",
            "{player} didn't check out kaboom kompetition",
            "{player} forgot that no one jumps for the beef",
            "{player} watched skibidi toilet",
            "{player} got banned from discord",
            "{player} got rejected",
            "{player} forgot to touch grass"
    );

    private static final List<String> randomizedKillMessages = List.of(
        "{player} got destroyed by {killer}",
        "{player} got smoked by {killer}",
        "{player} got obliterated by {killer}",
        "{player} got annihilated by {killer}",
        "{player} got wrecked by {killer}",
        "{player} was exploded by {killer}",
        "{player} was sniped by {killer}",
        "{player} was outplayed by {killer}",
        "{player} was outsmarted by {killer}",
        "{player} was outgunned by {killer}",
        "{player} was outmatched by {killer}",
        "{player} was outperformed by {killer}",
        "{player} was outdone by {killer}",
        "{player} was forced to watch skibidi toilet by {killer}",
        "{player} was banned from discord by {killer}",
        "{player} was rejected by {killer}",
        "{player} was forgotten by {killer}",
        "{player} was disrespected by {killer}"
    );

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.deathMessage(null);
        Player player = e.getEntity().getPlayer();
        if(player == null) return;
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 10, 1);
        Minigame activeMinigame = GameManager.currentMinigame;
        if(activeMinigame == null){
            doRandomizedDeathMessages(e);
        } else {
            if(activeMinigame.getFlags().contains(MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE)){
                doKillDeathMessage(e);
            } else {
                doRandomizedDeathMessages(e);
            }
        }
    }

    private static void doRandomizedDeathMessages(PlayerDeathEvent e){
        Player player = e.getEntity().getPlayer();
        if(player == null) return;
        String deathMessage = Utilities.getRandom(randomizedDeathMessages);


        Component deathMessageComponent = Component.text("💀 ")
                .append(Component.text(deathMessage.replace("{player}", Format.formatPlayerName(player)))
                        .color(Colors.GRAY))
                .replaceText(builder -> builder.matchLiteral(Format.formatPlayerName(player))
                        .replacement(Component.text(Format.formatPlayerName(player), Colors.WHITE)));


        Bukkit.broadcast(deathMessageComponent);
    }

    private static void doKillDeathMessage(PlayerDeathEvent e){
        Player player = e.getEntity().getPlayer();
        assert player != null;
        Player killer = player.getKiller();

        if(killer == null || killer == player){
            doRandomizedDeathMessages(e);
        } else {
            String deathMessage = Utilities.getRandom(randomizedKillMessages);

            Component killMessageComponent = Component.text("💀 ")
                    .append(Component.text(deathMessage.replace("{player}", Format.formatPlayerName(player))
                            .replace("{killer}", Format.formatPlayerName(killer)))
                            .color(Colors.GRAY))
                    .replaceText(builder -> builder.matchLiteral(Format.formatPlayerName(player))
                            .replacement(Component.text(Format.formatPlayerName(player), Colors.WHITE)))
                    .replaceText(builder -> builder.matchLiteral(Format.formatPlayerName(killer))
                            .replacement(Component.text(Format.formatPlayerName(killer), Colors.WHITE)));

            Bukkit.broadcast(killMessageComponent);
        }
    }
}
