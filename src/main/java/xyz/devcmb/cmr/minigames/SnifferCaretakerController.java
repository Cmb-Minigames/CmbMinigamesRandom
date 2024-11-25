package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;

public class SnifferCaretakerController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;

    public Entity redSniffer;
    public Entity blueSniffer;

    public int redSnifferHappiness = 0;
    public int blueSnifferHappiness = 0;
    public int happinessDecreaseAmount = 1;

    private final List<ItemStack> items = new ArrayList<>();

    public SnifferCaretakerController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");

        ItemStack speedPotion = new ItemStack(Material.POTION);
        PotionMeta speedPotionMeta = (PotionMeta) speedPotion.getItemMeta();
        if (speedPotionMeta == null) return;

        speedPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1), true);
        speedPotionMeta.setItemName("Speed Potion");
        speedPotion.setItemMeta(speedPotionMeta);

        ItemStack poisonSplashPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta poisonSplashPotionMeta = (PotionMeta) poisonSplashPotion.getItemMeta();
        if (poisonSplashPotionMeta == null) return;

        poisonSplashPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 10 * 20, 1), true);
        poisonSplashPotionMeta.setItemName("Splash Potion of Poison");
        poisonSplashPotion.setItemMeta(poisonSplashPotionMeta);

        ItemStack strengthPotion = new ItemStack(Material.POTION);
        PotionMeta strengthPotionMeta = (PotionMeta) strengthPotion.getItemMeta();
        if (strengthPotionMeta == null) return;

        strengthPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 1), true);
        strengthPotionMeta.setItemName("Potion of Strength");
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
            }
        }

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

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");

        if (redSpawn == null || blueSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        Map<String, Object> redBarrierFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("redTeamBarrier")).get("from");
        Map<String, Object> redBarrierTo = (Map<String, Object>)((Map<String, Object>) mapData.get("redTeamBarrier")).get("to");

        if (redBarrierFrom == null || redBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Red barrier points are not defined.");
            return;
        }

        Location redBarrierFromLocation = new Location(
                world,
                ((Number) redBarrierFrom.get("x")).doubleValue(),
                ((Number) redBarrierFrom.get("y")).doubleValue(),
                ((Number) redBarrierFrom.get("z")).doubleValue()
        );

        Location redBarrierToLocation = new Location(
                world,
                ((Number) redBarrierTo.get("x")).doubleValue(),
                ((Number) redBarrierTo.get("y")).doubleValue(),
                ((Number) redBarrierTo.get("z")).doubleValue()
        );

        Map<String, Object> blueBarrierFrom = (Map<String, Object>)((Map<String, Object>) mapData.get("blueTeamBarrier")).get("from");
        Map<String, Object> blueBarrierTo = (Map<String, Object>)((Map<String, Object>) mapData.get("blueTeamBarrier")).get("to");

        if (blueBarrierFrom == null || blueBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Blue barrier points are not defined.");
            return;
        }

        Location blueBarrierFromLocation = new Location(
                world,
                ((Number) blueBarrierFrom.get("x")).doubleValue(),
                ((Number) blueBarrierFrom.get("y")).doubleValue(),
                ((Number) blueBarrierFrom.get("z")).doubleValue()
        );

        Location blueBarrierToLocation = new Location(
                world,
                ((Number) blueBarrierTo.get("x")).doubleValue(),
                ((Number) blueBarrierTo.get("y")).doubleValue(),
                ((Number) blueBarrierTo.get("z")).doubleValue()
        );

        Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.BARRIER);
        Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.BARRIER);

        Map<String, Object> redSnifferSpawn = (Map<String, Object>) mapData.get("redTeamSnifferSpawn");
        Map<String, Object> blueSnifferSpawn = (Map<String, Object>) mapData.get("blueTeamSnifferSpawn");

        if (redSnifferSpawn == null || blueSnifferSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Sniffer spawns are not defined.");
            return;
        }

        Location redSnifferSpawnLocation = new Location(
                world,
                ((Number) redSnifferSpawn.get("x")).doubleValue(),
                ((Number) redSnifferSpawn.get("y")).doubleValue(),
                ((Number) redSnifferSpawn.get("z")).doubleValue()
        );

        Location blueSnifferSpawnLocation = new Location(
                world,
                ((Number) blueSnifferSpawn.get("x")).doubleValue(),
                ((Number) blueSnifferSpawn.get("y")).doubleValue(),
                ((Number) blueSnifferSpawn.get("z")).doubleValue()
        );

        redSniffer = world.spawnEntity(redSnifferSpawnLocation, EntityType.SNIFFER);
        blueSniffer = world.spawnEntity(blueSnifferSpawnLocation, EntityType.SNIFFER);
        redSniffer.setInvulnerable(true);
        blueSniffer.setInvulnerable(true);

        redSnifferHappiness = 300;
        blueSnifferHappiness = 300;
        happinessDecreaseAmount = 1;

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

        Map<String, Object> eventSpawnLocations = (Map<String, Object>) mapData.get("eventSpawnLocations");

        if (eventSpawnLocations == null) {
            CmbMinigamesRandom.LOGGER.warning("Event spawn area is not defined.");
            return;
        }

        Map<String, Object> spawnAreaFrom = (Map<String, Object>) eventSpawnLocations.get("from");
        Map<String, Object> spawnAreaTo = (Map<String, Object>) eventSpawnLocations.get("to");

        if (spawnAreaFrom == null || spawnAreaTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Event spawn area from and to are not defined.");
            return;
        }

        Location spawnAreaFromLocation = new Location(
                world,
                ((Number) spawnAreaFrom.get("x")).doubleValue(),
                ((Number) spawnAreaFrom.get("y")).doubleValue(),
                ((Number) spawnAreaFrom.get("z")).doubleValue()
        );

        Location spawnAreaToLocation = new Location(
                world,
                ((Number) spawnAreaTo.get("x")).doubleValue(),
                ((Number) spawnAreaTo.get("y")).doubleValue(),
                ((Number) spawnAreaTo.get("z")).doubleValue()
        );

        RED.forEach(player -> {
            player.teleport(Utilities.findValidLocation(redSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

        BLUE.forEach(player -> {
            player.teleport(Utilities.findValidLocation(blueSpawnLocation));
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
        });

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
                    if ((redSnifferHappiness == 0 || blueSnifferHappiness == 0) && !RED.isEmpty() && !BLUE.isEmpty()) endGame();
                }
            };

            happinessDepreciation.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 2);

            difficultyIncrease = new BukkitRunnable() {
                @Override
                public void run() {
                    happinessDecreaseAmount++;
                    allPlayers.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 10, 1));
                    Bukkit.broadcastMessage(ChatColor.RED + "The sniffers demand MORE! Happiness will go down by " + happinessDecreaseAmount + " every second!");
                }
            };

            difficultyIncrease.runTaskTimer(CmbMinigamesRandom.getPlugin(), 20 * 30, 20 * 30);

            itemSpawn = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= 5; i++) {
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
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Items have spawned around the map!");
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

        }, 20 * 10);


    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);

        redSniffer = null;
        blueSniffer = null;

        if(happinessDepreciation != null) {
            happinessDepreciation.cancel();
            happinessDepreciation = null;
        }

        if(difficultyIncrease != null) {
            difficultyIncrease.cancel();
            difficultyIncrease = null;
        }

        if(itemSpawn != null) {
            itemSpawn.cancel();
            itemSpawn = null;
        }

        if(sheepSpawn != null){
            sheepSpawn.cancel();
            sheepSpawn = null;
        }
        redSnifferHappiness = 0;
        blueSnifferHappiness = 0;

        Utilities.endGameResuable();
    }

    private void endGame() {
        GameManager.gameEnding = true;

        if (redSnifferHappiness == 0 && blueSnifferHappiness == 0) {
            Bukkit.getOnlinePlayers().forEach(plr -> {
                plr.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
        } else if(blueSnifferHappiness == 0){
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
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
        } else if(redSnifferHappiness == 0){
            RED.forEach(plr -> {
                plr.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                plr.getInventory().clear();
                plr.setGameMode(GameMode.SPECTATOR);
            });
            BLUE.forEach(plr -> {
                plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
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
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Sniffer Caretaker is currently active, and you have been added as a spectator.");
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
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                });
                return 7;
            } else if(BLUE.isEmpty()){
                GameManager.gameEnding = true;
                RED.forEach(plr -> {
                    plr.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                    plr.playSound(plr.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                    plr.getInventory().clear();
                    plr.setGameMode(GameMode.SPECTATOR);
                    Database.addUserStars(plr, getStarSources().get(StarSource.WIN).intValue());
                });

                return 7;
            }
        }

        return null;
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISABLE_PLAYER_DEATH_DROP,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.UNLIMITED_BLOCKS
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redTeamSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueTeamSpawn");
        World world = Bukkit.getWorld(worldName);

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.sniffercaretaker_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)blueSpawn.get("x")).doubleValue(), ((Number)blueSpawn.get("y")).doubleValue(), ((Number)blueSpawn.get("z")).doubleValue()));
        }
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
            player,
            CMScoreboardManager.mergeScoreboards(
                CMScoreboardManager.scoreboards.get("sniffercaretaker").getScoreboard(player),
                scoreboard
            )
        );
    }

    @Override
    public Map<StarSource, Number> getStarSources() {
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
        return "Keep your team’s sniffer alive by giving it food, dirt, and torchflower seeds stolen from the other team. You win if the other team’s sniffer is not taken care of enough and dies.";
    }
}
