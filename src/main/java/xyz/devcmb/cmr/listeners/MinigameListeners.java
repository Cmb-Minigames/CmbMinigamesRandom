package xyz.devcmb.cmr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.minigames.MinigameFlag;

import java.util.List;
import java.util.Map;

public class MinigameListeners implements Listener {
    private static final List<Material> teamBlocks = List.of(
        Material.RED_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.YELLOW_CONCRETE
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if(minigame.getFlags().contains(MinigameFlag.CANNOT_BREAK_BLOCKS)){
            event.setCancelled(true);
        } else if (minigame.getFlags().contains(MinigameFlag.DISABLE_BLOCK_DROPS)) {
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if(GameManager.currentMinigame == null || !GameManager.ingame) return;
            Minigame minigame = GameManager.currentMinigame;
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && minigame.getFlags().contains(MinigameFlag.DISABLE_FALL_DAMAGE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if(GameManager.currentMinigame == null || !GameManager.ingame) return;
            Minigame minigame = GameManager.currentMinigame;
            if (minigame.getFlags().contains(MinigameFlag.PVP_DISABLED)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if(minigame.getFlags().contains(MinigameFlag.CANNOT_PLACE_BLOCKS)){
            event.setCancelled(true);
        } else if(minigame.getFlags().contains(MinigameFlag.UNLIMITED_BLOCKS)){
            ItemStack itemInHand = event.getItemInHand();
            Bukkit.getScheduler().runTask(CmbMinigamesRandom.getPlugin(), () -> {
                itemInHand.setAmount(itemInHand.getAmount() + 1);
                if (event.getHand() == EquipmentSlot.HAND) {
                    event.getPlayer().getInventory().setItemInMainHand(itemInHand);
                } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                    event.getPlayer().getInventory().setItemInOffHand(itemInHand);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (GameManager.playersFrozen) {
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getY() != event.getTo().getY() ||
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        } else if(GameManager.currentMap != null){
            String worldName = (String)((Map<?,?>)GameManager.currentMap.get("map")).get("worldName");
            Number voidHeight = (Number)((Map<?,?>)GameManager.currentMap.get("map")).get("voidHeight");
            if(worldName.equals(event.getPlayer().getWorld().getName()) && event.getPlayer().getLocation().getY() < voidHeight.intValue()){
                event.getPlayer().setHealth(0);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        minigame.playerRespawn(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        minigame.playerDeath(event);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;

        if(minigame.getFlags().contains(MinigameFlag.DISABLE_OFF_HAND)){
            event.setCancelled(true);
        }
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if(minigame.getFlags().contains(MinigameFlag.DISABLE_OFF_HAND)){
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40){
                event.setCancelled(true);
            } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
                event.setCancelled(true);
            }
        }
    }



    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;

        if(minigame.getFlags().contains(MinigameFlag.DISABLE_OFF_HAND) && event.getPlayer().getInventory().getItemInOffHand().equals(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        } else if(minigame.getFlags().contains(MinigameFlag.UNLIMITED_BLOCKS) && teamBlocks.contains(event.getItemDrop().getItemStack().getType())){
            event.setCancelled(true);
        }
    }
}
