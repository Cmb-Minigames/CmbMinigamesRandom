package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.minigames.bases.Teams2MinigameBase;
import xyz.devcmb.cmr.utils.*;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;

import java.util.*;

/**
 * The Capture The Flag minigame controller
 */
public class CaptureTheFlagController extends Teams2MinigameBase implements Minigame {
    private boolean blueTaken = false;
    private boolean redTaken = false;
    public ItemDisplay redFlagEntity = null;
    public ItemDisplay blueFlagEntity = null;
    public int redScore = 0;
    public int blueScore = 0;
    private BukkitRunnable itemSpawnRunnable = null;
    private final List<ItemStack> items = new ArrayList<>();
    public Timer timer;

    public CaptureTheFlagController() {
        ItemStack harmingArrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta harmingArrowItemMeta = (PotionMeta) harmingArrow.getItemMeta();
        if (harmingArrowItemMeta == null) return;

        harmingArrowItemMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1), true);
        harmingArrowItemMeta.setItemName("Harming Arrow");
        harmingArrow.setItemMeta(harmingArrowItemMeta);

        ItemStack speedPotion = new ItemStack(Material.POTION);
        PotionMeta speedPotionMeta = (PotionMeta) speedPotion.getItemMeta();
        if (speedPotionMeta == null) return;

        speedPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1), true);
        speedPotionMeta.setItemName("Speed Potion");
        speedPotion.setItemMeta(speedPotionMeta);

        ItemStack poisonSplashPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta poisonSplashPotionMeta = (PotionMeta) poisonSplashPotion.getItemMeta();
        if (poisonSplashPotionMeta == null) return;

        poisonSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 8 * 20, 1), true);
        poisonSplashPotionMeta.setItemName("Splash Potion of Poison");
        poisonSplashPotion.setItemMeta(poisonSplashPotionMeta);

        ItemStack strengthPotion = new ItemStack(Material.POTION);
        PotionMeta strengthPotionMeta = (PotionMeta) strengthPotion.getItemMeta();
        if (strengthPotionMeta == null) return;

        strengthPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 10 * 20, 1), true);
        strengthPotionMeta.setItemName("Potion of Strength");
        strengthPotion.setItemMeta(strengthPotionMeta);

        items.add(harmingArrow);
        items.add(speedPotion);
        items.add(poisonSplashPotion);
        items.add(new ItemStack(Material.ENDER_PEARL));
        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        items.add(new ItemStack(Material.ARROW, 4));
        items.add(new ItemStack(Material.WIND_CHARGE, 3));
        items.add(strengthPotion);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        super.start();

        List<Map<String, Number>> itemSpawnLocations = (List<Map<String, Number>>) mapData.get("itemSpawnLocations");
        if(itemSpawnLocations == null) {
            CmbMinigamesRandom.LOGGER.warning("Item spawn locations are not defined.");
            return;
        }

        RED.forEach(player -> {
            player.teleport(Utilities.findValidLocation(redSpawn));
            Fade.fadePlayer(player, 0, 0, 40);
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawn));
            Fade.fadePlayer(player, 0, 0, 40);
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
        });

        GameManager.playersFrozen = true;
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());

        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setSaturation(0f);
                    Utilities.Countdown(player, 10);
                });

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        GameManager.playersFrozen = false;
                        RED.forEach(player -> {
                            Map<?, List<?>> kit = Kits.ctf_kit;
                            Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
                        });

                        BLUE.forEach(player -> {
                            Map<?, List<?>> kit = Kits.ctf_kit;
                            Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
                        });

                        timer = TimerManager.runTimer("ctf");

                        spawnRedFlag();
                        spawnBlueFlag();

                        itemSpawnRunnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                Map<String, Number> spawnLocationMap = (Map<String, Number>) Utilities.getRandom(itemSpawnLocations).get("location");
                                ItemStack item = Utilities.getRandom(items);

                                Location itemSpawnPlatform = new Location(world, spawnLocationMap.get("x").doubleValue(), spawnLocationMap.get("y").doubleValue(), spawnLocationMap.get("z").doubleValue());
                                Location itemSpawnLocation = new Location(world, spawnLocationMap.get("x").doubleValue(), spawnLocationMap.get("y").doubleValue() + 1, spawnLocationMap.get("z").doubleValue());
                                itemSpawnPlatform.getBlock().setType(Material.AIR);
                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
                                        Bukkit.broadcastMessage(ChatColor.GOLD + "An item has spawned at " + itemSpawnPlatform.getBlockX() + ", " + itemSpawnPlatform.getBlockY() + ", " + itemSpawnPlatform.getBlockZ() + "!");
                                        itemSpawnPlatform.getBlock().setType(Material.WHITE_CONCRETE);
                                        Bukkit.getOnlinePlayers().forEach(plr -> plr.playSound(plr.getLocation(), Sound.ENTITY_ITEM_PICKUP, 10, 1));

                                        Item itemEntity = world.dropItem(itemSpawnLocation, item);
                                        itemEntity.setPickupDelay(0);
                                        itemEntity.setVelocity(new Vector());
                                    }
                                }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 2);
                            }
                        };
                        itemSpawnRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 20);
                    }
                }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 10);
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 2);
    }

    private void spawnRedFlag(){
        Location redFlagLocation = Utilities.getLocationFromConfig(mapData, world, "flags.redFlag");
        if(redFlagLocation == null) {
            CmbMinigamesRandom.LOGGER.warning("Red flag location is not defined.");
            return;
        }

        redFlagEntity = (ItemDisplay) Objects.requireNonNull(world).spawnEntity(redFlagLocation, EntityType.ITEM_DISPLAY);
        ItemStack redFlagItem = new ItemStack(Material.ECHO_SHARD);

        ItemMeta meta1 = redFlagItem.getItemMeta();
        if(meta1 == null) return;
        meta1.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("red_flag").intValue());
        meta1.setItemName("Red Flag");

        redFlagItem.setItemMeta(meta1);
        redFlagEntity.setItemStack(redFlagItem);
    }

    private void spawnBlueFlag(){
        Location blueFlagLocation = Utilities.getLocationFromConfig(mapData, world, "flags.blueFlag");
        if(blueFlagLocation == null) {
            CmbMinigamesRandom.LOGGER.warning("Blue flag location is not defined.");
            return;
        }

        blueFlagEntity = (ItemDisplay) Objects.requireNonNull(world).spawnEntity(blueFlagLocation, EntityType.ITEM_DISPLAY);
        ItemStack blueFlagItem = new ItemStack(Material.ECHO_SHARD);

        ItemMeta meta2 = blueFlagItem.getItemMeta();
        if(meta2 == null) return;
        meta2.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("blue_flag").intValue());
        meta2.setItemName("Blue Flag");

        blueFlagItem.setItemMeta(meta2);
        blueFlagEntity.setItemStack(blueFlagItem);
    }

    @Override
    public void stop() {
        redFlagEntity.remove();
        blueFlagEntity.remove();
        redScore = 0;
        blueScore = 0;
        redTaken = false;
        blueTaken = false;
        timer = null;

        if(itemSpawnRunnable != null) itemSpawnRunnable.cancel();
        super.stop();
    }

    @SuppressWarnings("unchecked")
    public void handlePlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<?, ?> flags = (Map<?, ?>) mapData.get("flags");
        Map<String, Object> redFlag = (Map<String, Object>) flags.get("redFlag");
        Map<String, Object> blueFlag = (Map<String, Object>) flags.get("blueFlag");

        if (!player.getWorld().getName().equals(worldName) || event.getTo() == null) return;

        if(BLUE.contains(player)){
            if(player.getLocation().distanceSquared(new Location(Bukkit.getWorld(worldName), ((Number)redFlag.get("x")).doubleValue(), ((Number)redFlag.get("y")).doubleValue(), ((Number)redFlag.get("z")).doubleValue())) < 1 && !redTaken){
                redTaken = true;
                player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have captured the flag!");
                player.setGlowing(true);
                player.setHealth(10);
                Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(10);

                ItemStack redFlagItem = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = redFlagItem.getItemMeta();
                if(meta == null) return;
                meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("red_flag").intValue());
                meta.setItemName(ChatColor.RED + "Red Flag");
                redFlagItem.setItemMeta(meta);
                player.getInventory().setItemInOffHand(redFlagItem);

                redFlagEntity.remove();

                RED.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getName() + " has captured the flag! Stop them from reaching their base!"));
                BLUE.forEach(plr -> {
                    if(plr == player) return;
                    plr.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + player.getName() + " has captured the flag! Defend them!");
                });
            } else if(inBlueClaimZone(event.getTo())){
                if(getPlayerFlag(player).equals("red")){
                    player.getInventory().setItemInOffHand(null);
                    player.setGlowing(false);
                    blueScore++;
                    redTaken = false;
                    Database.addUserStars(player, getStarSources().get(StarSource.OBJECTIVE));
                    if(blueScore >= 3){
                        timer.end();
                        endGame("blue");
                    } else {
                        BLUE.forEach(plr -> {
                            plr.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "BLUE SCORE", "", 5, 25, 5);
                            plr.playSound(plr.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 10, 1);
                        });
                        RED.forEach(plr -> {
                            plr.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "BLUE SCORE", "", 5, 25, 5);
                            plr.playSound(plr.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 10, 1);
                        });
                        spawnRedFlag();
                    }
                }
                teleportToTeamBase(player);
            } else if(inRedClaimZone(event.getTo())){
                teleportToTeamBase(player);
            }
        } else if(RED.contains(event.getPlayer())){
            if(event.getPlayer().getLocation().distanceSquared(new Location(Bukkit.getWorld(worldName), ((Number)blueFlag.get("x")).doubleValue(), ((Number)blueFlag.get("y")).doubleValue(), ((Number)blueFlag.get("z")).doubleValue())) < 1 && !blueTaken){
                blueTaken = true;
                player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have captured the flag!");
                player.setGlowing(true);
                player.setHealth(10);
                Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(10);

                ItemStack blueFlagIcon = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = blueFlagIcon.getItemMeta();
                if(meta == null) return;
                meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("blue_flag").intValue());
                meta.setItemName(ChatColor.BLUE + "Blue Flag");
                blueFlagIcon.setItemMeta(meta);
                event.getPlayer().getInventory().setItemInOffHand(blueFlagIcon);

                blueFlagEntity.remove();

                BLUE.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getName() + " has captured the flag! Stop them from reaching their base!"));
                RED.forEach(plr -> {
                    if(plr == event.getPlayer()) return;
                    plr.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + player.getName() + " has captured the flag! Defend them!");
                });
            } else if(inRedClaimZone(event.getTo())){
                if(getPlayerFlag(player).equals("blue")){
                    player.getInventory().setItemInOffHand(null);
                    player.setGlowing(false);
                    redScore++;
                    blueTaken = false;
                    Database.addUserStars(player, getStarSources().get(StarSource.OBJECTIVE));
                    if(redScore >= 3) {
                        timer.end();
                        endGame("red");
                    } else {
                        BLUE.forEach(plr -> {
                            plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "RED SCORE", "", 5, 25, 5);
                            plr.playSound(plr.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 10, 1);
                        });
                        RED.forEach(plr -> {
                            plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "RED SCORE", "", 5, 25, 5);
                            plr.playSound(plr.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 10, 1);
                        });
                        spawnBlueFlag();
                    }
                }
                teleportToTeamBase(player);
            } else if(inBlueClaimZone(event.getTo())){
                teleportToTeamBase(player);
            }
        }
    }

    public void endGame(String winner){
        GameManager.gameEnding = true;
        timer = null;
        if(winner.equals("red")){
            RED.forEach(plr -> {
               plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
               Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
               plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
               plr.getInventory().clear();
               plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(winner.equals("blue")){
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else {
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 7);
    }

    @SuppressWarnings("unchecked")
    private boolean inRedClaimZone(Location location){
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        Map<?, ?> flags = (Map<?, ?>) mapData.get("flags");
        Map<String, Object> redFlagClaim = (Map<String, Object>) flags.get("redFlagClaim");
        Map<String, Number> from = (Map<String, Number>) redFlagClaim.get("from");
        Map<String, Number> to = (Map<String, Number>) redFlagClaim.get("to");

        return location.getX() >= Math.min(from.get("x").doubleValue(), to.get("x").doubleValue()) &&
            location.getX() <= Math.max(from.get("x").doubleValue(), to.get("x").doubleValue()) &&
            location.getY() >= Math.min(from.get("y").doubleValue(), to.get("y").doubleValue()) &&
            location.getY() <= Math.max(from.get("y").doubleValue(), to.get("y").doubleValue()) &&
            location.getZ() >= Math.min(from.get("z").doubleValue(), to.get("z").doubleValue()) &&
            location.getZ() <= Math.max(from.get("z").doubleValue(), to.get("z").doubleValue());
    }

    @SuppressWarnings("unchecked")
    private boolean inBlueClaimZone(Location location){
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        Map<?, ?> flags = (Map<?, ?>) mapData.get("flags");
        Map<String, Object> blueFlagClaim = (Map<String, Object>) flags.get("blueFlagClaim");
        Map<String, Number> from = (Map<String, Number>) blueFlagClaim.get("from");
        Map<String, Number> to = (Map<String, Number>) blueFlagClaim.get("to");

        return location.getX() >= Math.min(from.get("x").doubleValue(), to.get("x").doubleValue()) &&
           location.getX() <= Math.max(from.get("x").doubleValue(), to.get("x").doubleValue()) &&
           location.getY() >= Math.min(from.get("y").doubleValue(), to.get("y").doubleValue()) &&
           location.getY() <= Math.max(from.get("y").doubleValue(), to.get("y").doubleValue()) &&
           location.getZ() >= Math.min(from.get("z").doubleValue(), to.get("z").doubleValue()) &&
           location.getZ() <= Math.max(from.get("z").doubleValue(), to.get("z").doubleValue());
    }

    private String getPlayerFlag(Player player) {
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem.getType() == Material.ECHO_SHARD) {
            ItemMeta meta = offHandItem.getItemMeta();
            if (meta != null && meta.hasCustomModelData()) {
                int customModelData = meta.getCustomModelData();
                if (customModelData == CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("red_flag").intValue()) {
                    return "red";
                } else if (customModelData == CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("blue_flag").intValue()) {
                    return "blue";
                } else {
                    CmbMinigamesRandom.LOGGER.warning("Invalid custom model data found, got " + customModelData);
                }
            } else {
                CmbMinigamesRandom.LOGGER.warning("No custom model data found");
            }
        } else {
            CmbMinigamesRandom.LOGGER.warning("No off-hand item found");
        }
        return "none";
    }

    public void teleportToTeamBase(Player player){
        CmbMinigamesRandom.LOGGER.info("Player respawn event called from ctf controller");
        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        if (RED.contains(player)) {
            player.teleport(redSpawn);
        } else if (BLUE.contains(player)) {
            player.teleport(blueSpawn);
        }

        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getDefaultValue());

        revokeFlag(player);
    }

    private void revokeFlag(Player player){
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem.getType() == Material.ECHO_SHARD && offHandItem.getItemMeta() != null) {
            if (offHandItem.getItemMeta().getItemName().equals(ChatColor.RED + "Red Flag")) {
                player.setGlowing(false);
                spawnRedFlag();
                RED.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The red flag has been returned!"));
                BLUE.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The red flag has been returned!"));
                redTaken = false;
            } else if (offHandItem.getItemMeta().getItemName().equals(ChatColor.BLUE + "Blue Flag")) {
                player.setGlowing(false);
                spawnBlueFlag();
                RED.forEach(plr -> plr.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "The blue flag has been returned!"));
                BLUE.forEach(plr -> plr.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "The blue flag has been returned!"));
                blueTaken = false;
            }

            player.getInventory().setItemInOffHand(null);
        }
    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("ctf").getScoreboard(player)
        );
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
        return Map.of(
            StarSource.KILL, 2,
            StarSource.WIN, 20,
            StarSource.OBJECTIVE, 5
        );
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.DISABLE_OFF_HAND,
            MinigameFlag.DISABLE_BLOCK_DROPS,
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.USE_CUSTOM_RESPAWN
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueSpawn");
        World world = Bukkit.getWorld(worldName);

        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);

        Location redSpawnLocation = new Location(
                world,
                ((Number) redSpawn.get("x")).doubleValue(),
                ((Number) redSpawn.get("y")).doubleValue(),
                ((Number) redSpawn.get("z")).doubleValue()
        );

        Location blueSpawnLocation = new Location(
                world,
                ((Number) blueSpawn.get("x")).doubleValue(),
                ((Number) blueSpawn.get("y")).doubleValue(),
                ((Number) blueSpawn.get("z")).doubleValue()
        );

        redSpawnLocation.setYaw(((Number) redSpawn.get("yaw")).floatValue());
        redSpawnLocation.setPitch(((Number) redSpawn.get("pitch")).floatValue());

        blueSpawnLocation.setYaw(((Number) blueSpawn.get("yaw")).floatValue());
        blueSpawnLocation.setPitch(((Number) blueSpawn.get("pitch")).floatValue());

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.ctf_kit, player, Material.RED_CONCRETE);

            event.setRespawnLocation(redSpawnLocation);
            event.getPlayer().teleport(redSpawnLocation);
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.ctf_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(blueSpawnLocation);
            event.getPlayer().teleport(blueSpawnLocation);
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {
        Player player = Objects.requireNonNull(event.getEntity().getPlayer());
        revokeFlag(player);
    }

    @Override
    public Boolean dontReturnBlock(BlockPlaceEvent event) {
        Location redFlagLocation = redFlagEntity.getLocation();
        Location blueFlagLocation = blueFlagEntity.getLocation();
        Location blockLocation = event.getBlock().getLocation();

        int blockPlacingDistance = 5;
        return blockLocation.distance(redFlagLocation) <= blockPlacingDistance || blockLocation.distance(blueFlagLocation) <= blockPlacingDistance;
    }

    @Override
    public String getId() {
        return "ctf";
    }

    @Override
    public String getName() {
        return "Capture the Flag";
    }

    @Override
    public String getDescription() {
        return "Steal the flag from the opposing team and return it to your base by jumping into your teams capture point! First to 3 wins!";
    }
}