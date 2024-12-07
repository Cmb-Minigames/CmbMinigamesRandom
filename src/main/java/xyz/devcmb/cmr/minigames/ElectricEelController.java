package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.*;
import xyz.devcmb.cmr.listeners.minigames.ElectricEelListeners;

import java.util.*;

public class ElectricEelController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;
    public List<Beam> beams = new ArrayList<>();

    public int redUranium = 0;
    public int blueUranium = 0;
    public boolean hasStarted = false;
    public int timeLeft = 0;
    private BukkitRunnable timerTick;

    public ElectricEelController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");
    }

    @SuppressWarnings("unchecked")
    public void ResetBeams(Location eelLocation) {
        beams.forEach(Beam::Remove);
        beams.clear();

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");

        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Location redStorageFromLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "from");
        Location redStorageToLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "to");

        Location blueStorageFromLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "from");
        Location blueStorageToLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "to");

        Location pollutorBeamLocation = Utilities.getLocationFromConfig(mapData, world, "pollutorBeam");

        assert redStorageFromLocation != null;
        assert redStorageToLocation != null;
        assert blueStorageFromLocation != null;
        assert blueStorageToLocation != null;
        assert pollutorBeamLocation != null;

        InitalizeBeams(redStorageFromLocation, redStorageToLocation, pollutorBeamLocation, world);
        InitalizeBeams(blueStorageFromLocation, blueStorageToLocation, eelLocation, world);
    }

    private void InitalizeBeams(Location from, Location to, Location beamLocation, World world) {
        int redMinX = Math.min(from.getBlockX(), to.getBlockX());
        int redMinY = Math.min(from.getBlockY(), to.getBlockY());
        int redMinZ = Math.min(from.getBlockZ(), to.getBlockZ());
        int redMaxX = Math.max(from.getBlockX(), to.getBlockX());
        int redMaxY = Math.max(from.getBlockY(), to.getBlockY());
        int redMaxZ = Math.max(from.getBlockZ(), to.getBlockZ());

        for (int x = redMinX; x <= redMaxX; x++) {
            for (int y = redMinY; y <= redMaxY; y++) {
                for (int z = redMinZ; z <= redMaxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() == Material.NETHER_QUARTZ_ORE) {
                        beams.add(new Beam(block.getLocation(), beamLocation));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        ElectricEelListeners.NullLocations();
        hasStarted = false;
        Utilities.gameStartReusable();
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
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.RED);
            } else {
                BLUE.add(allPlayers.get(i));
                blueTeam.addEntry(allPlayers.get(i).getName());
                GameManager.teamColors.put(allPlayers.get(i), ChatColor.BLUE);

                PotionEffect dolphinsGrace = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 255, false, false);
                allPlayers.get(i).addPotionEffect(dolphinsGrace);

                PotionEffect waterBreathing = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255, false, false);
                allPlayers.get(i).addPotionEffect(waterBreathing);
            }
        }

        redUranium = 6;
        blueUranium = 6;
        timeLeft = 60 * 4;

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Location redSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "redSpawn");
        Location blueSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "blueSpawn");
        Location electricEelLocation = Utilities.getLocationFromConfig(mapData, world, "electricEelSpawn");
        assert redSpawnLocation != null;
        assert blueSpawnLocation != null;

        RED.forEach(player -> {
            player.teleport(Utilities.findValidLocation(redSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "POLLUTER" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "EEL" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        Location redBarrierFromLocation = Utilities.getLocationFromConfig(mapData, world, "redBarrier", "from");
        Location redBarrierToLocation = Utilities.getLocationFromConfig(mapData, world, "redBarrier", "to");

        assert redBarrierFromLocation != null;
        assert redBarrierToLocation != null;

        Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.BARRIER);

        assert electricEelLocation != null;
        LivingEntity electricEel = (LivingEntity) world.spawnEntity(electricEelLocation, EntityType.SALMON);
        Objects.requireNonNull(electricEel.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(3.0);
        electricEel.setAI(false);
        electricEel.setInvulnerable(true);
        electricEel.setRemoveWhenFarAway(false);

        ResetBeams(electricEelLocation);

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            hasStarted = true;
            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);

            Map<?, List<?>> kit = Kits.electriceel_kit;
            RED.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
                player.setSaturation(0);
                player.setHealth(20);
            });
            BLUE.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
                player.setSaturation(0);
                player.setHealth(20);
            });

            timerTick = new BukkitRunnable() {
                @Override
                public void run() {
                    if(timeLeft <= 0){
                        this.cancel();
                        endGame();
                        return;
                    }

                    timeLeft--;
                }
            };

            timerTick.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20, 20);
        }, 20 * 10);
    }

    @Override
    public void stop() {
        for (Player player : RED) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }

        for (Player player : BLUE) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }

        beams.forEach(Beam::Remove); // Sorry for causing 10000 warnings in the console!!! :P
        beams.clear();
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);

        redUranium = 0;
        blueUranium = 0;
        timeLeft = 0;

        if(timerTick != null) timerTick.cancel();

        Utilities.endGameResuable();
    }

    public void endGame() {
        GameManager.gameEnding = true;
        if (redUranium > blueUranium) {
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if (blueUranium > redUranium) {
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(CmbMinigamesRandom.getPlugin(), 20 * 7);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Electric Eel is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        RED.remove(player);
        BLUE.remove(player);

        if(CmbMinigamesRandom.DeveloperMode){
            return (RED.isEmpty() && BLUE.isEmpty()) ? 0 : null;
        } else {
            if(RED.isEmpty()){
                GameManager.gameEnding = true;
                BLUE.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                });
                return 7;
            } else if(BLUE.isEmpty()){
                GameManager.gameEnding = true;
                RED.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                });

                return 7;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        Location redSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "redSpawn");
        Location blueSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "blueSpawn");

        assert redSpawnLocation != null;
        assert blueSpawnLocation != null;

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.electriceel_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(redSpawnLocation);
            player.teleport(redSpawnLocation);
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.electriceel_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(blueSpawnLocation);
            player.teleport(blueSpawnLocation);

            PotionEffect dolphinsGrace = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 255, false, false);
            player.addPotionEffect(dolphinsGrace);

            PotionEffect waterBreathing = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255, false, false);
            player.addPotionEffect(waterBreathing);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (offhand.getType() != Material.NETHER_QUARTZ_ORE) return;

        ItemMeta meta = offhand.getItemMeta();
        assert meta != null;

        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        Location fromLocation;
        Location toLocation;

        if (Objects.equals(meta.getPersistentDataContainer().get(new NamespacedKey(CmbMinigamesRandom.getPlugin(), "team"), PersistentDataType.STRING), "RED")) {
            fromLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "from");
            toLocation = Utilities.getLocationFromConfig(mapData, world, "redStorage", "to");
            redUranium++;
        } else {
            fromLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "from");
            toLocation = Utilities.getLocationFromConfig(mapData, world, "blueStorage", "to");
            blueUranium++;
        }

        assert fromLocation != null;
        assert toLocation != null;

        Random random = new Random();

        while(true) {
            int spawnX = random.nextInt(fromLocation.getBlockX(), toLocation.getBlockX());
            int spawnZ = random.nextInt(fromLocation.getBlockZ(), toLocation.getBlockZ());

            if(world.getBlockAt(spawnX, fromLocation.getBlockY(), spawnZ).getType() != Material.NETHER_QUARTZ_ORE) {
                world.getBlockAt(spawnX, fromLocation.getBlockY(), spawnZ).setType(Material.NETHER_QUARTZ_ORE);
                break;
            }
        }
    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
            player,
            CMScoreboardManager.mergeScoreboards(
                CMScoreboardManager.scoreboards.get("electriceel").getScoreboard(player),
                scoreboard
            )
        );
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
        return Map.of(
                StarSource.WIN, 15,
                StarSource.KILL, 2,
                StarSource.OBJECTIVE, 5
        );
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
                MinigameFlag.DISABLE_FALL_DAMAGE,
                MinigameFlag.DISABLE_OFF_HAND,
                MinigameFlag.DISABLE_BLOCK_DROPS,
                MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
                MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
                MinigameFlag.USE_CUSTOM_RESPAWN,
                MinigameFlag.UNLIMITED_BLOCKS
        );
    }

    @Override
    public Boolean dontReturnBlock(BlockPlaceEvent event) {
        return event.getBlock().getType() == Material.NETHER_QUARTZ_ORE;
    }

    @Override
    public String getId() {
        return "electriceel";
    }

    @Override
    public String getName() {
        return "Electric Eel";
    }

    @Override
    public String getDescription() {
        return "Steal uranium from the other team to either power your King Electric Eel or your nuclear reactor. The team with the most uranium at the end wins!";
    }
}
