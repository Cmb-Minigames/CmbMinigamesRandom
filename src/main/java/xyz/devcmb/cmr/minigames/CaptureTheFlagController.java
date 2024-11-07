package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.scoreboards.minigames.CTFScoreboard;
import xyz.devcmb.cmr.utils.CustomModelDataConstants;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

public class CaptureTheFlagController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    private final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;
    private boolean blueTaken = false;
    private boolean redTaken = false;
    private ItemDisplay redFlagEntity = null;
    private ItemDisplay blueFlagEntity = null;
    public int redScore = 0;
    public int blueScore = 0;

    public CaptureTheFlagController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");
        redTeam.setColor(ChatColor.RED);
        blueTeam.setColor(ChatColor.BLUE);
    }

    @Override
    public void start() {
        List<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(allPlayers);

        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);

        for (int i = 0; i < allPlayers.size(); i++) {
            if (i % 2 == 0) {
                RED.add(allPlayers.get(i));
                redTeam.addEntry(allPlayers.get(i).getName());
                allPlayers.get(i).setScoreboard(scoreboard);
            } else {
                BLUE.add(allPlayers.get(i));
                blueTeam.addEntry(allPlayers.get(i).getName());
                allPlayers.get(i).setScoreboard(scoreboard);
            }
        }

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");

        if (redSpawn == null || blueSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        String worldName = (String) mapData.get("worldName");
        if (Bukkit.getWorld(worldName) == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Location redSpawnLocation = new Location(
                Bukkit.getWorld(worldName),
                ((Number) redSpawn.get("x")).doubleValue(),
                ((Number) redSpawn.get("y")).doubleValue(),
                ((Number) redSpawn.get("z")).doubleValue()
        );

        Location blueSpawnLocation = new Location(
                Bukkit.getWorld(worldName),
                ((Number) blueSpawn.get("x")).doubleValue(),
                ((Number) blueSpawn.get("y")).doubleValue(),
                ((Number) blueSpawn.get("z")).doubleValue()
        );

        RED.forEach(player -> {
            player.teleport(findValidLocation(redSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
        });

        BLUE.forEach(player -> {
            player.teleport(findValidLocation(blueSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
        });

        GameManager.playersFrozen = true;
        Bukkit.getOnlinePlayers().forEach(player -> { player.getInventory().clear(); });

        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> Utilities.Countdown(player, 10));
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        GameManager.playersFrozen = false;
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            Map<?, List<?>> kit = Kits.ctf_kit;
                            Kits.kitPlayer(kit, player, RED.contains(player) ? Material.RED_CONCRETE : Material.BLUE_CONCRETE);
                        });
                        @SuppressWarnings("unchecked")
                        Map<String, ?> redFlag = ((Map<String,?>)((Map<String,?>)mapData.get("flags")).get("redFlag"));
                        @SuppressWarnings("unchecked")
                        Map<String, ?> blueFlag = ((Map<String,?>)((Map<String,?>)mapData.get("flags")).get("blueFlag"));
                        spawnRedFlag(worldName, redFlag);
                        spawnBlueFlag(worldName, blueFlag);
                    }
                }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 10);
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 2);
    }

    private Location findValidLocation(Location spawnLocation) {
        Location newLocation = spawnLocation.clone();
        int radius = 1;
        int maxRadius = 10;
        boolean validLocationFound = false;

        while (!validLocationFound && radius <= maxRadius) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    newLocation.add(x, 0, z);
                    if (newLocation.getBlock().getType().isSolid() &&
                            newLocation.add(0, 1, 0).getBlock().getType() == Material.AIR &&
                            newLocation.add(0, 1, 0).getBlock().getType() == Material.AIR &&
                            Objects.requireNonNull(newLocation.getWorld()).getNearbyEntities(newLocation, 1, 1, 1).isEmpty()) {
                        validLocationFound = true;
                        break;
                    }
                    newLocation.subtract(x, 2, z);
                }
                if (validLocationFound) break;
            }
            radius++;
        }

        if (!validLocationFound) {
            return spawnLocation;
        }

        return newLocation;
    }

    private void spawnRedFlag(String worldName, Map<String, ?> redFlag){
        redFlagEntity = (ItemDisplay) Objects.requireNonNull(Bukkit.getWorld(worldName)).spawnEntity(new Location(Bukkit.getWorld(worldName), ((Number)redFlag.get("x")).doubleValue(), ((Number)redFlag.get("y")).doubleValue(), ((Number)redFlag.get("z")).doubleValue()), EntityType.ITEM_DISPLAY);
        ItemStack redFlagItem = new ItemStack(Material.ECHO_SHARD);
        redFlagEntity.setItemStack(new ItemStack(Material.ECHO_SHARD));


        ItemMeta meta1 = redFlagItem.getItemMeta();
        meta1.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("red_flag").intValue());
        meta1.setItemName("Red Flag");

        redFlagItem.setItemMeta(meta1);
        redFlagEntity.setItemStack(redFlagItem);
    }

    private void spawnBlueFlag(String worldName, Map<String, ?> blueFlag){
        blueFlagEntity = (ItemDisplay)Bukkit.getWorld(worldName).spawnEntity(new Location(Bukkit.getWorld(worldName), ((Number)blueFlag.get("x")).doubleValue(), ((Number)blueFlag.get("y")).doubleValue(), ((Number)blueFlag.get("z")).doubleValue()), EntityType.ITEM_DISPLAY);

        ItemStack blueFlagItem = new ItemStack(Material.ECHO_SHARD);
        blueFlagEntity.setItemStack(new ItemStack(Material.ECHO_SHARD));

        ItemMeta meta2 = blueFlagItem.getItemMeta();
        if(meta2 == null) return;
        meta2.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("blue_flag").intValue());
        meta2.setItemName("Blue Flag");

        blueFlagItem.setItemMeta(meta2);
        blueFlagEntity.setItemStack(blueFlagItem);
    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);
        redFlagEntity.remove();
        blueFlagEntity.remove();
        redScore = 0;
        blueScore = 0;
        redTaken = false;
        blueTaken = false;

        GameManager.prepare();
    }

    @Override
    public void playerJoin(Player player) {
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");

        player.sendMessage(ChatColor.RED + "A game of Capture the Flag is currently active, and you have been added as a spectator.");
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(new Location(Bukkit.getWorld(worldName), ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
    }

    @Override
    public void playerLeave(Player player) {
        // TODO
    }

    public void handlePlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<?, ?> flags = (Map<?, ?>) mapData.get("flags");
        Map<String, Object> redFlag = (Map<String, Object>) flags.get("redFlag");
        Map<String, Object> blueFlag = (Map<String, Object>) flags.get("blueFlag");

        if (!player.getWorld().getName().equals(worldName) || event.getTo() == null) return;

        if(BLUE.contains(player)){
            if(player.getLocation().distanceSquared(new Location(Bukkit.getWorld(worldName), ((Number)redFlag.get("x")).doubleValue(), ((Number)redFlag.get("y")).doubleValue(), ((Number)redFlag.get("z")).doubleValue())) < 1 && !redTaken){
                redTaken = true;
                player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have captured the flag!");
                player.setHealth(6);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(6);

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
            } else if(inBlueClaimZone(event.getTo())){ // formatting my beloved
                if(getPlayerFlag(player).equals("red")){
                    player.getInventory().setItemInOffHand(null);
                    blueScore++;
                    redTaken = false;
                    if(blueScore >= 3){
                        endGame("blue");
                    } else {
                        BLUE.forEach(plr -> plr.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "BLUE SCORE", "", 5, 25, 5));
                        RED.forEach(plr -> plr.sendTitle(ChatColor.BLUE + ChatColor.BOLD.toString() + "BLUE SCORE", "", 5, 25, 5));
                        spawnRedFlag(worldName, redFlag);
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
                player.setHealth(6);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(6);

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
                    redScore++;
                    blueTaken = false;
                    if(redScore >= 3) {
                        endGame("red");
                    } else {
                        BLUE.forEach(plr -> plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "RED SCORE", "", 5, 25, 5));
                        RED.forEach(plr -> plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "RED SCORE", "", 5, 25, 5));
                        spawnBlueFlag(worldName, blueFlag);
                    }
                }
                teleportToTeamBase(player);
            } else if(inBlueClaimZone(event.getTo())){
                teleportToTeamBase(player);
            }
        }
    }

    private void endGame(String winner){
        GameManager.gameEnding = true;
        if(winner.equals("red")){
            RED.forEach(plr -> {
               plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
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
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

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

        if (RED.contains(player)) {
            player.teleport(redSpawnLocation);
        } else if (BLUE.contains(player)) {
            player.teleport(blueSpawnLocation);
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());

        revokeFlag(player);
    }

    private void revokeFlag(Player player){
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");

        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && offHandItem.getType() == Material.ECHO_SHARD) {
            if (offHandItem.getItemMeta().getItemName().equals(ChatColor.RED + "Red Flag")) {
                spawnRedFlag(worldName, (Map<String, ?>) ((Map<String, ?>) mapData.get("flags")).get("redFlag"));
                RED.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The red flag has been returned!"));
                BLUE.forEach(plr -> plr.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The red flag has been returned!"));
                redTaken = false;
            } else if (offHandItem.getItemMeta().getItemName().equals(ChatColor.BLUE + "Blue Flag")) {
                spawnBlueFlag(worldName, (Map<String, ?>) ((Map<String, ?>) mapData.get("flags")).get("blueFlag"));
                RED.forEach(plr -> plr.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "The blue flag has been returned!"));
                BLUE.forEach(plr -> plr.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "The blue flag has been returned!"));
                blueTaken = false;
            }

            player.getInventory().setItemInOffHand(null);
        }
    }

    @Override
    public void updateScoreboard(Player player) {
        CTFScoreboard.displayCTFScoreboard(player, this);
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.DISABLE_OFF_HAND,
            MinigameFlag.DISABLE_BLOCK_DROPS
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");
        World world = Bukkit.getWorld(worldName);

        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);

        if(RED.contains(player)){
            event.setRespawnLocation(new Location(world, ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
        } else if(BLUE.contains(player)){
            event.setRespawnLocation(new Location(world, ((Number)blueSpawn.get("x")).doubleValue(), ((Number)blueSpawn.get("y")).doubleValue(), ((Number)blueSpawn.get("z")).doubleValue()));
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {
        Player player = Objects.requireNonNull(event.getEntity().getPlayer());
        revokeFlag(player);
    }

    @Override
    public String getName() {
        return "Capture the Flag";
    }

    @Override
    public String getDescription() {
        return "Steal the flag from the opposing team and return it to your base!";
    }
}