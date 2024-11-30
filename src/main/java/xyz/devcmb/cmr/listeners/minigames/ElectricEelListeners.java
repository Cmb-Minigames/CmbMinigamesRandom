package xyz.devcmb.cmr.listeners.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.ElectricEelController;

public class ElectricEelListeners implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE) {
            ItemStack uranium = new ItemStack(Material.NETHER_QUARTZ_ORE, 1);
            ItemMeta meta = uranium.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.GREEN + "Uranium");
            uranium.setItemMeta(meta);
            event.getPlayer().getInventory().setItemInOffHand(uranium);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ElectricEelController electricEelController = (ElectricEelController) GameManager.getMinigameByName("Electric Eel");
        if(electricEelController == null || GameManager.currentMinigame != electricEelController) return;

        Player player = event.getPlayer();

        if(electricEelController.RED.contains(event.getPlayer())) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER) {
                PotionEffect dolphinsGrace = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 255, false, false);
                player.addPotionEffect(dolphinsGrace);

                PotionEffect waterBreathing = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255, false, false);
                player.addPotionEffect(waterBreathing);
            } else {
                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1, false, false);
                player.addPotionEffect(slowness);

                PotionEffect miningFatigue = new PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 0, false, false);
                player.addPotionEffect(miningFatigue);
            }
        }
    }
}
