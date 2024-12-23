package xyz.devcmb.cmr.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
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
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

/**
 * The Sniffer Caretaker minigame controller
 */
public class SnifferCaretakerController extends Teams2MinigameBase implements Minigame {
    public Entity redSniffer;
    public Entity blueSniffer;

    private Location redBaseFromLocation;
    private Location redBaseToLocation;
    private Location blueBaseFromLocation;
    private Location blueBaseToLocation;

    public int redSnifferHappiness = 0;
    public int blueSnifferHappiness = 0;
    public int happinessDecreaseAmount = 1;

    private final List<ItemStack> items = new ArrayList<>();
    public Timer timer;

    public SnifferCaretakerController() {
        ItemStack speedPotion = new ItemStack(Material.POTION);
        PotionMeta speedPotionMeta = (PotionMeta) speedPotion.getItemMeta();
        if (speedPotionMeta == null) return;

        speedPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1), true);
        speedPotionMeta.displayName(Component.text("Speed Potion"));
        speedPotion.setItemMeta(speedPotionMeta);

        ItemStack poisonSplashPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta poisonSplashPotionMeta = (PotionMeta) poisonSplashPotion.getItemMeta();
        if (poisonSplashPotionMeta == null) return;

        poisonSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 10 * 20, 1), true);
        poisonSplashPotionMeta.displayName(Component.text("Splash Potion of Poison"));
        poisonSplashPotion.setItemMeta(poisonSplashPotionMeta);

        ItemStack strengthPotion = new ItemStack(Material.POTION);
        PotionMeta strengthPotionMeta = (PotionMeta) strengthPotion.getItemMeta();
        if (strengthPotionMeta == null) return;

        strengthPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 1), true);
        strengthPotionMeta.displayName(Component.text("Potion of Strength"));
        strengthPotion.setItemMeta(strengthPotionMeta);

        items.add(speedPotion);
        items.add(poisonSplashPotion);
        items.add(strengthPotion);
        items.add(new ItemStack(Material.ENDER_PEARL));
        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        items.add(new ItemStack(Material.WIND_CHARGE, 3));
        items.add(new ItemStack(Material.IRON_SWORD));
        items.add(new ItemStack(Material.HAY_BLOCK, 1));
        items.add(new ItemStack(Material.MUTTON, 2));
    }

    private BukkitRunnable happinessDepreciation;
    private BukkitRunnable difficultyIncrease;
    private BukkitRunnable itemSpawn;
    private BukkitRunnable sheepSpawn;

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        super.start();

        Location redBarrierFromLocation = Utilities.getLocationFromConfig(mapData, world, "redTeamBarrier.from");
        Location redBarrierToLocation = Utilities.getLocationFromConfig(mapData, world, "redTeamBarrier.to");

        Location blueBarrierFromLocation = Utilities.getLocationFromConfig(mapData, world, "blueTeamBarrier.from");
        Location blueBarrierToLocation = Utilities.getLocationFromConfig(mapData, world, "blueTeamBarrier.to");

        assert redBarrierFromLocation != null;
        Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.BARRIER);
        assert blueBarrierFromLocation != null;
        Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.BARRIER);

        Location redSnifferSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "redTeamSnifferSpawn");
        Location blueSnifferSpawnLocation = Utilities.getLocationFromConfig(mapData, world, "blueTeamSnifferSpawn");

        assert redSnifferSpawnLocation != null;
        redSniffer = world.spawnEntity(redSnifferSpawnLocation, EntityType.SNIFFER);
        assert blueSnifferSpawnLocation != null;
        blueSniffer = world.spawnEntity(blueSnifferSpawnLocation, EntityType.SNIFFER);

        redSniffer.setInvulnerable(true);
        blueSniffer.setInvulnerable(true);

        redSnifferHappiness = 300;
        blueSnifferHappiness = 300;
        happinessDecreaseAmount = 1;

        Location spawnAreaFromLocation = Utilities.getLocationFromConfig(mapData, world, "eventSpawnLocations", "from");
        Location spawnAreaToLocation = Utilities.getLocationFromConfig(mapData, world, "eventSpawnLocations", "to");

        Map<String, Object> redBaseFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("redBasePreventPlace")).get("from");
        Map<String, Object> redBaseTo = (Map<String, Object>)((Map<String, Object>) mapData.get("redBasePreventPlace")).get("to");

        if (redBaseFrom == null || redBaseTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Red base points are not defined.");
            return;
        }

        redBaseFromLocation = Utilities.getLocationFromConfig(mapData, world, "redBasePreventPlace", "from");
        redBaseToLocation = Utilities.getLocationFromConfig(mapData, world, "redBasePreventPlace", "to");

        blueBaseFromLocation = Utilities.getLocationFromConfig(mapData, world, "blueBasePreventPlace", "from");
        blueBaseToLocation = Utilities.getLocationFromConfig(mapData, world, "blueBasePreventPlace", "to");

        RED.forEach(player -> {
            assert redSpawn != null;
            player.teleport(Utilities.findValidLocation(redSpawn));
            Fade.fadePlayer(player, 0, 0, 40);

            Component teamText = Component.text("You are on the ")
                    .append(Component.text("RED").color(Colors.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text(" team!"));

            player.sendMessage(teamText);
        });

        BLUE.forEach(player -> {
            assert blueSpawn != null;
            player.teleport(Utilities.findValidLocation(blueSpawn));
            Fade.fadePlayer(player, 0, 0, 40);

            Component teamText = Component.text("You are on the ")
                    .append(Component.text("BLUE").color(Colors.BLUE).decorate(TextDecoration.BOLD))
                    .append(Component.text(" team!"));

            player.sendMessage(teamText);
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            RED.forEach(player -> Utilities.Countdown(player, 10));
            BLUE.forEach(player -> Utilities.Countdown(player, 10));
        }, 20 * 2);

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Map<?, List<?>> kit = Kits.sniffercaretaker_kit;
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

            timer = TimerManager.runTimer("sniffercaretaker");

            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);
            Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.AIR);

            happinessDepreciation = new BukkitRunnable() {
                @Override
                public void run() {
                    redSnifferHappiness = Math.clamp(redSnifferHappiness - happinessDecreaseAmount, 0, 1000);
                    blueSnifferHappiness = Math.clamp(blueSnifferHappiness - happinessDecreaseAmount, 0, 1000);
                    if (GameManager.gameEnding) {
                        this.cancel();
                        return;
                    }
                    if ((redSnifferHappiness <= 0 || blueSnifferHappiness <= 0) && (CmbMinigamesRandom.DeveloperMode ? !(RED.isEmpty() && BLUE.isEmpty()) : (!RED.isEmpty() && !BLUE.isEmpty()))) {
                        timer.end();
                        endGame();
                    }
                }
            };

            happinessDepreciation.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 2);

            difficultyIncrease = new BukkitRunnable() {
                @Override
                public void run() {
                    happinessDecreaseAmount++;
                    allPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 10, 1));

                    Component message = Component.text("The sniffers demand MORE! Happiness will go down by ")
                            .append(Component.text(happinessDecreaseAmount).decorate(TextDecoration.BOLD))
                            .append(Component.text(" every second!")).color(Colors.RED);

                    Bukkit.broadcast(message);
                }
            };

            difficultyIncrease.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20 * 30, 20 * 30);

            itemSpawn = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= 5; i++) {
                        assert spawnAreaFromLocation != null;
                        assert spawnAreaToLocation != null;
                        int spawnX = new Random().nextInt(spawnAreaFromLocation.getBlockX(), spawnAreaToLocation.getBlockX());
                        int spawnZ = new Random().nextInt(spawnAreaFromLocation.getBlockZ(), spawnAreaToLocation.getBlockZ());

                        Block block = world.getBlockAt(spawnX, spawnAreaToLocation.getBlockY(), spawnZ);
                        Block blockBelow = world.getBlockAt(spawnX, spawnAreaToLocation.getBlockY() - 1, spawnZ);

                        if (block.getType() == Material.AIR && blockBelow.getType() == Material.GRASS_BLOCK) {
                            Item itemEntity = world.dropItem(block.getLocation(), items.get(new Random().nextInt(items.size())));
                            itemEntity.setPickupDelay(0);
                            itemEntity.setVelocity(new Vector());
                        } else {
                            i--;
                        }
                    }

                    allPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 10, 1));
                    Bukkit.broadcast(Component.text("Items have spawned around the map!").color(Colors.GREEN));
                }
            };

            itemSpawn.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20 * 20, 20 * 20);

            sheepSpawn = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= 15; i++) {
                        int sheepCount = 0;

                        for (Entity entity : world.getEntities()) {
                            if (entity.getType() == EntityType.SHEEP) {
                                sheepCount++;
                            }
                        }

                        if (sheepCount >= 15) {
                            break;
                        }

                        assert spawnAreaFromLocation != null;
                        assert spawnAreaToLocation != null;
                        int spawnX = new Random().nextInt(spawnAreaFromLocation.getBlockX(), spawnAreaToLocation.getBlockX());
                        int spawnZ = new Random().nextInt(spawnAreaFromLocation.getBlockZ(), spawnAreaToLocation.getBlockZ());

                        Block block = world.getBlockAt(spawnX, spawnAreaToLocation.getBlockY(), spawnZ);
                        Block blockBelow = world.getBlockAt(spawnX, spawnAreaToLocation.getBlockY() - 1, spawnZ);

                        if (block.getType() == Material.AIR && blockBelow.getType() == Material.GRASS_BLOCK) {
                            world.spawnEntity(block.getLocation(), EntityType.SHEEP);
                        } else {
                            i--;
                        }
                    }
                }
            };

            sheepSpawn.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20 * 3, 20 * 3);

        }, 20 * 12);
    }

    @Override
    public void stop() {
        timer = null;
        super.stop();

        redSniffer = null;
        blueSniffer = null;

        happinessDepreciation.cancel();
        happinessDepreciation = null;
        difficultyIncrease.cancel();
        difficultyIncrease = null;
        itemSpawn.cancel();
        itemSpawn = null;
        sheepSpawn.cancel();
        sheepSpawn = null;

        redSnifferHappiness = 0;
        blueSnifferHappiness = 0;
    }

    @SuppressWarnings("all")
    public void endGame() {
        timer = null;
        GameManager.gameEnding = true;

        Title victoryTitle = Title.title(
                Component.text("VICTORY").color(Colors.GOLD).decorate(TextDecoration.BOLD),
                Component.text(""),
                Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        Title defeatTitle = Title.title(
                Component.text("DEFEAT").color(Colors.RED).decorate(TextDecoration.BOLD),
                Component.text(""),
                Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        Title drawTitle = Title.title(
                Component.text("DRAW").color(Colors.AQUA).decorate(TextDecoration.BOLD),
                Component.text(""),
                Title.Times.times(Utilities.ticksToMilliseconds(5), Utilities.ticksToMilliseconds(80), Utilities.ticksToMilliseconds(10))
        );

        if (redSnifferHappiness == blueSnifferHappiness) {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.showTitle(drawTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(redSnifferHappiness > blueSnifferHappiness){
            RED.forEach(plr -> {
                plr.showTitle(victoryTitle);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.showTitle(defeatTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(blueSnifferHappiness > redSnifferHappiness){
            RED.forEach(plr -> {
                plr.showTitle(defeatTitle);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.showTitle(victoryTitle);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN));
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

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.USE_CUSTOM_RESPAWN
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(redSpawn);
            player.teleport(redSpawn);
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(blueSpawn);
            player.teleport(blueSpawn);
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {}

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("sniffercaretaker").getScoreboard(player)
        );
    }

    @Override
    public Boolean dontReturnBlock(BlockPlaceEvent event) {
        return Utilities.isWithin(event.getBlock().getLocation(), redBaseFromLocation, redBaseToLocation) || Utilities.isWithin(event.getBlock().getLocation(), blueBaseFromLocation, blueBaseToLocation);
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
        return Map.of(
                StarSource.KILL, 2,
                StarSource.WIN, 20,
                StarSource.OBJECTIVE, 1
        );
    }

    @Override
    public String getId() {
        return "sniffercaretaker";
    }

    @Override
    public String getName() {
        return "Sniffer Caretaker";
    }

    @Override
    public String getDescription() {
        return "Keep your team’s sniffer alive by giving it food, dirt, and other resources found around the map. You win if the other team’s sniffer is not taken care of enough and dies.";
    }
}