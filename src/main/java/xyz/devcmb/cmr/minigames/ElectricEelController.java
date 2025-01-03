package xyz.devcmb.cmr.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.Fade;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.minigames.bases.Teams2MinigameBase;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;
import xyz.devcmb.cmr.utils.*;
import xyz.devcmb.cmr.listeners.minigames.ElectricEelListeners;

import java.util.*;

/**
 * The Electric Eel minigame controller
 */
public class ElectricEelController extends Teams2MinigameBase implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public List<Beam> beams = new ArrayList<>();

    public int redUranium = 0;
    public int blueUranium = 0;
    public boolean hasStarted = false;
    public Timer timer;

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

        for (int i = 0; i < allPlayers.size(); i++) {
            if (i % 2 == 0) {
                RED.add(allPlayers.get(i));
                GameManager.teamColors.put(allPlayers.get(i), NamedTextColor.RED);
            } else {
                BLUE.add(allPlayers.get(i));
                GameManager.teamColors.put(allPlayers.get(i), NamedTextColor.BLUE);

                PotionEffect dolphinsGrace = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 255, false, false);
                allPlayers.get(i).addPotionEffect(dolphinsGrace);

                PotionEffect waterBreathing = new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 255, false, false);
                allPlayers.get(i).addPotionEffect(waterBreathing);
            }
        }

        redUranium = 6;
        blueUranium = 6;

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
            Fade.fadePlayer(player, 0, 0, 40);

            Component teamText = Component.text("You are on the ")
                .append(Component.text("POLLUTER").color(Colors.RED).decorate(TextDecoration.BOLD))
                .append(Component.text(" team!"));

            player.sendMessage(teamText);
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawnLocation));
            Fade.fadePlayer(player, 0, 0, 40);

            Component teamText = Component.text("You are on the ")
                .append(Component.text("EEL").color(Colors.BLUE).decorate(TextDecoration.BOLD))
                .append(Component.text(" team!"));

            player.sendMessage(teamText);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            RED.forEach(player -> Utilities.Countdown(player, 10));
            BLUE.forEach(player -> Utilities.Countdown(player, 10));
        }, 20 * 2);

        Location redBarrierFromLocation = Utilities.getLocationFromConfig(mapData, world, "redBarrier", "from");
        Location redBarrierToLocation = Utilities.getLocationFromConfig(mapData, world, "redBarrier", "to");

        assert redBarrierFromLocation != null;
        assert redBarrierToLocation != null;

        Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.BARRIER);

        assert electricEelLocation != null;
        LivingEntity electricEel = (LivingEntity) world.spawnEntity(electricEelLocation, EntityType.SALMON);
        Objects.requireNonNull(electricEel.getAttribute(Attribute.SCALE)).setBaseValue(3.0);
        electricEel.setAI(false);
        electricEel.setInvulnerable(true);
        electricEel.setRemoveWhenFarAway(false);

        ResetBeams(electricEelLocation);

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            hasStarted = true;
            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);

            timer = TimerManager.runTimer("electriceel");

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
        }, 20 * 12);
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

        redUranium = 0;
        blueUranium = 0;

        timer = null;

        Utilities.endGameResuable();
    }

    public void endGame() {
        timer = null;
        GameManager.gameEnding = true;

        Title victoryTitle = Title.title(
            Component.text("VICTORY").color(Colors.GOLD).decorate(TextDecoration.BOLD),
            Component.empty(),
            Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        Title defeatTitle = Title.title(
            Component.text("DEFEAT").color(Colors.RED).decorate(TextDecoration.BOLD),
            Component.empty(),
            Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        Title drawTitle = Title.title(
            Component.text("DRAW").color(Colors.AQUA).decorate(TextDecoration.BOLD),
            Component.empty(),
            Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        if (redUranium > blueUranium) {
            RED.forEach(plr -> {
                plr.showTitle(victoryTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.showTitle(defeatTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if (blueUranium > redUranium) {
            BLUE.forEach(plr -> {
                plr.showTitle(victoryTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            RED.forEach(plr -> {
                plr.showTitle(defeatTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.showTitle(drawTitle);
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
            CMScoreboardManager.scoreboards.get("electriceel").getScoreboard(player)
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
