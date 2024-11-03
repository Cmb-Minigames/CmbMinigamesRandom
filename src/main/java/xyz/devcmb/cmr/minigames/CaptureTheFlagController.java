package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private Map<String, ?> currentMap = null;
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
    public void start(Map<String, ?> map) {
        currentMap = map;
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

        Map<String, Object> mapData = (Map<String, Object>) map.get("map");
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
                            newLocation.getWorld().getNearbyEntities(newLocation, 1, 1, 1).isEmpty()) {
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
    }

    @Override
    public void playerJoin(Player player) {
        Map<String, Object> mapData = (Map<String, Object>) currentMap.get("map");
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
        Map<String, Object> mapData = (Map<String, Object>) currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<?, ?> flags = (Map<?, ?>) mapData.get("flags");
        Map<String, Object> redFlag = (Map<String, Object>) flags.get("redFlag");
        Map<String, Object> blueFlag = (Map<String, Object>) flags.get("blueFlag");

        if(BLUE.contains(event.getPlayer())){
            if(event.getPlayer().getLocation().distanceSquared(new Location(Bukkit.getWorld(worldName), ((Number)redFlag.get("x")).doubleValue(), ((Number)redFlag.get("y")).doubleValue(), ((Number)redFlag.get("z")).doubleValue())) < 1 && !redTaken){
                redTaken = true;
                event.getPlayer().sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have captured the flag!");
                event.getPlayer().setHealth(6);

                ItemStack redFlagItem = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = redFlagItem.getItemMeta();
                meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("red_flag").intValue());
                meta.setItemName(ChatColor.RED + "Red Flag");
                redFlagItem.setItemMeta(meta);
                event.getPlayer().getInventory().setItemInOffHand(redFlagItem);

                redFlagEntity.remove();

                RED.forEach(player -> player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + event.getPlayer().getName() + " has captured the flag! Stop them from reaching their base!"));
                BLUE.forEach(player -> {
                    if(player == event.getPlayer()) return;
                    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + event.getPlayer().getName() + " has captured the flag! Defend them!");
                });
            }
        } else if(RED.contains(event.getPlayer())){
            if(event.getPlayer().getLocation().distanceSquared(new Location(Bukkit.getWorld(worldName), ((Number)blueFlag.get("x")).doubleValue(), ((Number)blueFlag.get("y")).doubleValue(), ((Number)blueFlag.get("z")).doubleValue())) < 1 && !blueTaken){
                blueTaken = true;
                event.getPlayer().sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have captured the flag!");
                event.getPlayer().setHealth(6);

                ItemStack blueFlagIcon = new ItemStack(Material.ECHO_SHARD);
                ItemMeta meta = blueFlagIcon.getItemMeta();
                meta.setCustomModelData(CustomModelDataConstants.constants.get(Material.ECHO_SHARD).get("blue_flag").intValue());
                meta.setItemName(ChatColor.BLUE + "Blue Flag");
                blueFlagIcon.setItemMeta(meta);
                event.getPlayer().getInventory().setItemInOffHand(blueFlagIcon);

                blueFlagEntity.remove();

                BLUE.forEach(player -> player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + event.getPlayer().getName() + " has captured the flag! Stop them from reaching their base!"));
                RED.forEach(player -> {
                    if(player == event.getPlayer()) return;
                    player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + event.getPlayer().getName() + " has captured the flag! Defend them!");
                });
            }
        }
    }

    @Override
    public void playerRespawn(Player player){
        Map<String, Object> mapData = (Map<String, Object>) currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");

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

        if(RED.contains(player)){
            player.teleport(redSpawnLocation);
        } else if(BLUE.contains(player)){
            player.teleport(blueSpawnLocation);
        }

        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
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
            MinigameFlag.DISABLE_OFF_HAND
        );
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