package xyz.devcmb.cmr.listeners;

import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.minigames.Minigame;
import xyz.devcmb.cmr.minigames.MinigameFlag;
import xyz.devcmb.cmr.minigames.StarSource;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Format;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.List;
import java.util.Map;

public class MinigameListeners implements Listener {
    private static final List<Material> teamBlocks = List.of(
        Material.RED_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.YELLOW_CONCRETE
    );

    private static final List<Material> noRepeatTools = List.of(
        Material.WOODEN_SWORD,
        Material.WOODEN_AXE,
        Material.WOODEN_PICKAXE,
        Material.WOODEN_SHOVEL,
        Material.WOODEN_HOE,
        Material.STONE_SWORD,
        Material.STONE_AXE,
        Material.STONE_PICKAXE,
        Material.STONE_SHOVEL,
        Material.STONE_HOE,
        Material.IRON_SWORD,
        Material.IRON_AXE,
        Material.IRON_PICKAXE,
        Material.IRON_SHOVEL,
        Material.IRON_HOE,
        Material.GOLDEN_SWORD,
        Material.GOLDEN_AXE,
        Material.GOLDEN_PICKAXE,
        Material.GOLDEN_SHOVEL,
        Material.GOLDEN_HOE,
        Material.DIAMOND_SWORD,
        Material.DIAMOND_AXE,
        Material.DIAMOND_PICKAXE,
        Material.DIAMOND_SHOVEL,
        Material.DIAMOND_HOE,
        Material.NETHERITE_SWORD,
        Material.NETHERITE_AXE,
        Material.NETHERITE_PICKAXE,
        Material.NETHERITE_SHOVEL,
        Material.NETHERITE_HOE,
        // Armor
        Material.LEATHER_HELMET,
        Material.LEATHER_CHESTPLATE,
        Material.LEATHER_LEGGINGS,
        Material.LEATHER_BOOTS,
        Material.CHAINMAIL_HELMET,
        Material.CHAINMAIL_CHESTPLATE,
        Material.CHAINMAIL_LEGGINGS,
        Material.CHAINMAIL_BOOTS,
        Material.IRON_HELMET,
        Material.IRON_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS,
        Material.DIAMOND_HELMET,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_HELMET,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_BOOTS
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;

        if(minigame.getFlags().contains(MinigameFlag.USE_CUSTOM_RESPAWN)){
            if (event.getEntity() instanceof Player player) {
                double finalHealth = player.getHealth() - event.getFinalDamage();
                if (finalHealth <= 0) {
                    event.setCancelled(true);
                    Utilities.customRespawn(player, event.getDamageSource());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if(minigame.getFlags().contains(MinigameFlag.CANNOT_PLACE_BLOCKS)){
            event.setCancelled(true);
        } else if(minigame.getFlags().contains(MinigameFlag.UNLIMITED_BLOCKS) && event.getBlock().getType() != Material.TNT){
            ItemStack itemInHand = event.getItemInHand();
            if(minigame.dontReturnBlock(event)) return;
            if (itemInHand.getType() != Material.STONE_HOE) {
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
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(GameManager.gameEnding) return;
        if(event.getTo() == null) return;
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if (GameManager.playersFrozen) {
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getY() != event.getTo().getY() ||
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        } else if(GameManager.currentMap != null && MapLoader.LOADED_MAP != null){
            String worldName = MapLoader.LOADED_MAP;
            Number voidHeight = (Number)((Map<?,?>)GameManager.currentMap.get("map")).get("voidHeight");
            if(worldName.equals(event.getPlayer().getWorld().getName()) && event.getPlayer().getLocation().getY() < voidHeight.intValue()){
                if(minigame.getFlags().contains(MinigameFlag.USE_CUSTOM_RESPAWN)){
                    Utilities.customRespawn(event.getPlayer(), DamageSource.builder(DamageType.OUT_OF_WORLD).build());
                } else {
                    event.getPlayer().setHealth(0);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) {
            event.setRespawnLocation(new Location(Bukkit.getWorld("pregame"), -26.5, -43.5, -18));
            return;
        }

        Minigame minigame = GameManager.currentMinigame;
        minigame.playerRespawn(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        Player player = event.getEntity().getPlayer();
        if(player == null) {
            CmbMinigamesRandom.LOGGER.warning("Player is null in playerDeathEvent.");
            return;
        }
        Player killer = player.getKiller();
        if(minigame.getFlags().contains(MinigameFlag.DISABLE_PLAYER_DEATH_DROP) && !event.getDrops().isEmpty()){
            event.getDrops().clear();
        }

        minigame.playerDeath(event);
        if(killer != null && minigame.getStarSources().containsKey(StarSource.KILL) && killer != player){
            killer.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 10, 1);
            killer.sendTitle("⚔ " + Format.formatPlayerName(player), "+" + minigame.getStarSources().get(StarSource.KILL).intValue() + " 🌟", 0, 40, 10);
            Database.addUserStars(killer, minigame.getStarSources().get(StarSource.KILL).intValue());
            GameManager.kills.put(killer, GameManager.kills.get(killer).intValue() + 1);
        }
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
        } else if(minigame.getFlags().contains(MinigameFlag.NO_REPEATED_TOOLS)){
            if (event.getClickedInventory() == null || event.getClickedInventory() == event.getWhoClicked().getInventory()) return;
            if (!(event.getWhoClicked() instanceof Player player)) return;

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(clickedItem) && noRepeatTools.contains(item.getType())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot have more than one item of this type in your inventory in this minigame!");
                    return;
                }

                for (ItemStack armorItem : player.getInventory().getArmorContents()) {
                    if (armorItem != null && armorItem.isSimilar(clickedItem) && noRepeatTools.contains(armorItem.getType())) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You cannot have more than one item of this type in your inventory in this minigame!");
                        return;
                    }
                }
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;

        if (minigame.getFlags().contains(MinigameFlag.DO_NOT_CONSUME_FIREWORKS)) {
            Player player = event.getPlayer();
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.FIREWORK_ROCKET && player.isGliding()) {
                    Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1)), 3 * 20); // delay 3 seconds before giving another one
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        if(GameManager.currentMinigame == null || !GameManager.ingame) return;
        Minigame minigame = GameManager.currentMinigame;
        if(minigame.getFlags().contains(MinigameFlag.CANNOT_TRAMPLE_FARMLAND)){
            event.setCancelled(true);
        }
    }
}
