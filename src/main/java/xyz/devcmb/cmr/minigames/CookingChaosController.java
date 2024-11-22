package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CookingChaosController implements Minigame {
    public List<Player> RED = new ArrayList<>();
    public List<Player> BLUE = new ArrayList<>();
    public final Scoreboard scoreboard;
    private final Team redTeam;
    private final Team blueTeam;
    private BukkitRunnable boneMealChestRefill;
    private BukkitRunnable customerRunnable;
    private BukkitRunnable timerRunnable;

    private final List<EntityType> customerEntities = List.of(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.BLAZE,
        EntityType.CREEPER,
        EntityType.PIGLIN,
        EntityType.PIGLIN_BRUTE,
        EntityType.PILLAGER,
        EntityType.BREEZE,
        EntityType.BOGGED
    );

    private final List<Material> customerOrders = List.of(
        Material.PUMPKIN_PIE,
        Material.BREAD,
        Material.GOLDEN_APPLE,
        Material.GLISTERING_MELON_SLICE,
        Material.COOKED_MUTTON,
        Material.COOKED_CHICKEN,
        Material.PUMPKIN_PIE,
        Material.COOKED_PORKCHOP
    );

    private final Map<Material, String> fontItems = Map.ofEntries(
        Map.entry(Material.PUMPKIN_PIE, "\uE00F"),
        Map.entry(Material.BREAD, "\uE010"),
        Map.entry(Material.GOLDEN_APPLE, "\uE011"),
        Map.entry(Material.GLISTERING_MELON_SLICE, "\uE012"),
        Map.entry(Material.COOKED_MUTTON, "\uE013"),
        Map.entry(Material.COOKED_CHICKEN, "\uE014"),
        Map.entry(Material.COOKED_PORKCHOP, "\uE015")
    );

    public List<Map<String, ?>> blueCustomers = new ArrayList<>();
    public List<Map<String, ?>> redCustomers = new ArrayList<>();

    public Integer redScore = 0;
    public Integer blueScore = 0;

    public final List<Map<String, Object>> blueTables = new ArrayList<>();
    public final List<Map<String, Object>> redTables = new ArrayList<>();

    public Integer timeLeft = 0;

    public CookingChaosController() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        redTeam = scoreboard.registerNewTeam("Red");
        blueTeam = scoreboard.registerNewTeam("Blue");
    }

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

        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueSpawn");

        if (redSpawn == null || blueSpawn == null) {
            CmbMinigamesRandom.LOGGER.warning("Spawn points are not defined.");
            return;
        }

        String worldName = (String) mapData.get("worldName");
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


        Map<String, Object> redBarrier = (Map<String, Object>) mapData.get("redBarrier");
        Map<String, Object> blueBarrier = (Map<String, Object>) mapData.get("blueBarrier");

        if (redBarrier == null || blueBarrier == null) {
            CmbMinigamesRandom.LOGGER.warning("Barrier points are not defined.");
            return;
        }

        Map<String, Object> redBarrierFrom = (Map<String, Object>) redBarrier.get("from");
        Map<String, Object> redBarrierTo = (Map<String, Object>) redBarrier.get("to");

        Map<String, Object> blueBarrierFrom = (Map<String, Object>) blueBarrier.get("from");
        Map<String, Object> blueBarrierTo = (Map<String, Object>) blueBarrier.get("to");

        if (redBarrierFrom == null || redBarrierTo == null || blueBarrierFrom == null || blueBarrierTo == null) {
            CmbMinigamesRandom.LOGGER.warning("Barrier points are not defined.");
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

        RED.forEach(player -> {
            player.teleport(redSpawnLocation);
            player.sendMessage("You are on the " + ChatColor.RED + ChatColor.BOLD + "RED" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 4, false, false, false));
        });

        BLUE.forEach(player -> {
            player.teleport(blueSpawnLocation);
            player.sendMessage("You are on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" + ChatColor.RESET + " team!");
            Utilities.Countdown(player, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 4, false, false, false));
        });

        List<Map<String, Map<String, Number>>> bonemealChests = (List<Map<String, Map<String, Number>>>) mapData.get("boneMealChests");
        Map<String, Object> redEntrance = (Map<String, Object>) mapData.get("redEntrance");
        Map<String, Object> blueEntrance = (Map<String, Object>) mapData.get("blueEntrance");

        if (bonemealChests == null || redEntrance == null || blueEntrance == null) {
            CmbMinigamesRandom.LOGGER.warning("Chests or entrances are not defined.");
            return;
        }

        Location redEntranceLocation = new Location(
            world,
            ((Number) redEntrance.get("x")).doubleValue(),
            ((Number) redEntrance.get("y")).doubleValue(),
            ((Number) redEntrance.get("z")).doubleValue()
        );

        Location blueEntranceLocation = new Location(
            world,
            ((Number) blueEntrance.get("x")).doubleValue(),
            ((Number) blueEntrance.get("y")).doubleValue(),
            ((Number) blueEntrance.get("z")).doubleValue()
        );

        List<Map<String, List<Map<String, Number>>>> redTeamTables = (List<Map<String, List<Map<String, Number>>>>) mapData.get("redTables");
        List<Map<String, List<Map<String, Number>>>> blueTeamTables = (List<Map<String, List<Map<String, Number>>>>) mapData.get("blueTables");

        if(redTeamTables == null || blueTeamTables == null){
            CmbMinigamesRandom.LOGGER.warning("Tables are not defined.");
            return;
        }

        redTeamTables.forEach(table -> {
            Map<String, Object> newTable = new HashMap<>();
            newTable.put("seatLocations", new ArrayList<>());
            newTable.put("customers", new ArrayList<>());

            List<Map<String, Number>> seats = table.get("seats");
            seats.forEach(seat -> {
                Map<String, Number> location = (Map<String, Number>) seat.get("location");
                double x = location.get("x").doubleValue();
                double y = location.get("y").doubleValue();
                double z = location.get("z").doubleValue();
                float yaw = location.get("yaw").floatValue();
                float pitch = location.get("pitch").floatValue();

                Location seatLocation = new Location(world, x, y, z, yaw, pitch);
                ((List<Location>)newTable.get("seatLocations")).add(seatLocation);
            });

            newTable.put("taken", false);
            redTables.add(newTable);
            CmbMinigamesRandom.LOGGER.info("A new red table has been registered");
        });

        blueTeamTables.forEach(table -> {
            Map<String, Object> newTable = new HashMap<>();
            newTable.put("seatLocations", new ArrayList<>());
            newTable.put("customers", new ArrayList<>());

            List<Map<String, Number>> seats = table.get("seats");
            seats.forEach(seat -> {
                Map<String, Number> location = (Map<String, Number>) seat.get("location");
                double x = location.get("x").doubleValue();
                double y = location.get("y").doubleValue();
                double z = location.get("z").doubleValue();
                float yaw = location.get("yaw").floatValue();
                float pitch = location.get("pitch").floatValue();

                Location seatLocation = new Location(world, x, y, z, yaw, pitch);
                ((List<Location>)newTable.get("seatLocations")).add(seatLocation);
            });

            newTable.put("taken", false);
            blueTables.add(newTable);

            CmbMinigamesRandom.LOGGER.info("A new blue table has been registered");
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Utilities.fillBlocks(redBarrierFromLocation, redBarrierToLocation, Material.AIR);
            Utilities.fillBlocks(blueBarrierFromLocation, blueBarrierToLocation, Material.AIR);

            timeLeft = 10 * 60;

            RED.forEach(player -> {
                Map<?, List<?>> kit = Kits.cookingchaos_kit;
                Kits.kitPlayer(kit, player, Material.RED_CONCRETE);
                player.setSaturation(0.0f);
            });

            BLUE.forEach(player -> {
                Map<?, List<?>> kit = Kits.cookingchaos_kit;
                Kits.kitPlayer(kit, player, Material.BLUE_CONCRETE);
                player.setSaturation(0.0f);
            });

            boneMealChestRefill = new BukkitRunnable() {
                @Override
                public void run() {
                    bonemealChests.forEach(chest -> {
                        Map<String, Number> chestLoc = chest.get("location");
                        Location chestLocation = new Location(
                            world,
                            chestLoc.get("x").doubleValue(),
                            chestLoc.get("y").doubleValue(),
                            chestLoc.get("z").doubleValue()
                        );

                        BlockState blockData = chestLocation.getBlock().getState();
                        if(!(blockData instanceof Chest chestData)) {
                            CmbMinigamesRandom.LOGGER.warning("Chest at " + chestLocation + " is not a chest.");
                        } else {
                            chestData.getInventory().clear();
                            chestData.getInventory().setItem(10, new ItemStack(Material.APPLE, 3));
                            chestData.getInventory().setItem(11, new ItemStack(Material.WHEAT_SEEDS, 12));
                            chestData.getInventory().setItem(12, new ItemStack(Material.MELON_SEEDS, 2));
                            chestData.getInventory().setItem(13, new ItemStack(Material.PUMPKIN_SEEDS, 2));
                            chestData.getInventory().setItem(14, new ItemStack(Material.CARROT, 1));
                            chestData.getInventory().setItem(15, new ItemStack(Material.BONE_MEAL, 16));
                            chestData.getInventory().setItem(16, new ItemStack(Material.SUGAR_CANE, 3));
                        }
                    });
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Crop chests have been refilled!");
                }
            };

            boneMealChestRefill.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 60 * 2);

            customerRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(!redTables.stream().allMatch(table -> (Boolean) table.get("taken"))){
                        Map<String, Object> selectedTable;
                        int selectedTableIndex;

                        do {
                            selectedTableIndex = new Random().nextInt(redTables.size());
                            selectedTable = redTables.get(selectedTableIndex);
                        } while ((Boolean) selectedTable.get("taken"));

                        AtomicInteger i = new AtomicInteger(); // what is this

                        int finalSelectedTableIndex = selectedTableIndex;
                        ((List<Location>)selectedTable.get("seatLocations")).forEach(loc -> {
                            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                                Map<String, Object> newCustomer = new HashMap<>();
                                Entity spawnedEntity = world.spawnEntity(redEntranceLocation, Utilities.getRandom(customerEntities));
                                spawnedEntity.setInvulnerable(true);
                                ((LivingEntity) spawnedEntity).setAI(false);
                                ((LivingEntity) spawnedEntity).setRemoveWhenFarAway(false);
                                newCustomer.put("entity", spawnedEntity);
                                newCustomer.put("order", Utilities.getRandom(customerOrders));
                                newCustomer.put("tableIndex", finalSelectedTableIndex);
                                ((List<Entity>)redTables.get(finalSelectedTableIndex).get("customers")).add(spawnedEntity);

                                Location textLocation = loc.clone().add(0, 2, 0);

                                TextDisplay text = (TextDisplay) world.spawnEntity(textLocation, EntityType.TEXT_DISPLAY);
                                text.setText(fontItems.get(newCustomer.get("order")));
                                text.setBillboard(Display.Billboard.CENTER);

                                newCustomer.put("orderTextEntity", text);

                                redCustomers.add(newCustomer);
                                Utilities.moveEntity(spawnedEntity, loc, 5 * 20);
                            }, 10L * i.get());
                            i.getAndIncrement();
                        });

                        selectedTable.put("taken", true);
                        RED.forEach(player -> player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "A customer has arrived at your resturant!"));
                    }

                    if(!blueTables.stream().allMatch(table -> (Boolean) table.get("taken"))){
                        Map<String, Object> selectedTable;
                        int selectedTableIndex;

                        do {
                            selectedTableIndex = new Random().nextInt(blueTables.size());
                            selectedTable = blueTables.get(selectedTableIndex);
                        } while ((Boolean) selectedTable.get("taken"));

                        AtomicInteger i = new AtomicInteger(); // what is this

                        int finalSelectedTableIndex = selectedTableIndex;
                        ((List<Location>)selectedTable.get("seatLocations")).forEach(loc -> {
                            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                                Map<String, Object> newCustomer = new HashMap<>();
                                Entity spawnedEntity = world.spawnEntity(blueEntranceLocation, Utilities.getRandom(customerEntities));
                                spawnedEntity.setInvulnerable(true);
                                ((LivingEntity) spawnedEntity).setAI(false);
                                ((LivingEntity) spawnedEntity).setRemoveWhenFarAway(false);
                                newCustomer.put("entity", spawnedEntity);
                                newCustomer.put("order", Utilities.getRandom(customerOrders));
                                newCustomer.put("tableIndex", finalSelectedTableIndex);

                                ((List<Entity>)blueTables.get(finalSelectedTableIndex).get("customers")).add(spawnedEntity);

                                Location textLocation = loc.clone().add(0, 2, 0);

                                TextDisplay text = (TextDisplay) world.spawnEntity(textLocation, EntityType.TEXT_DISPLAY);
                                text.setText(fontItems.get(newCustomer.get("order")));
                                text.setBillboard(Display.Billboard.CENTER);

                                newCustomer.put("orderTextEntity", text);

                                blueCustomers.add(newCustomer);
                                Utilities.moveEntity(spawnedEntity, loc, 5 * 20);
                            }, 10L * i.get());
                            i.getAndIncrement();
                        });

                        selectedTable.put("taken", true);
                        BLUE.forEach(player -> player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "A customer has arrived at your resturant!"));
                    }

                    CmbMinigamesRandom.LOGGER.info("Sent new customers to tables");
                }
            };
            customerRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20 * 40);

            timerRunnable = new BukkitRunnable() {
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
            timerRunnable.runTaskTimer(CmbMinigamesRandom.getPlugin(), 0, 20);
        }, 10 * 20);
    }

    public void endGame(){
        GameManager.gameEnding = true;

        if(boneMealChestRefill != null) boneMealChestRefill.cancel();
        boneMealChestRefill = null;
        if(customerRunnable != null) customerRunnable.cancel();
        customerRunnable = null;
        if(timerRunnable != null) timerRunnable.cancel();
        timerRunnable = null;

        RED.forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);

            if(redScore > blueScore){
                player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
                Database.addUserStars(player, getStarSources().get(StarSource.WIN).intValue());
            } else if(blueScore > redScore){
                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            }
        });

        BLUE.forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);

            if(blueScore > redScore){
                player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
                Database.addUserStars(player, getStarSources().get(StarSource.WIN).intValue());
            } else if(redScore > blueScore){
                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "DRAW", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            }
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::stop, 8 * 20);
    }

    @Override
    public void stop() {
        RED.clear();
        BLUE.clear();
        redTeam.getEntries().forEach(redTeam::removeEntry);
        blueTeam.getEntries().forEach(blueTeam::removeEntry);
        if(boneMealChestRefill != null) boneMealChestRefill.cancel();
        boneMealChestRefill = null;
        if(customerRunnable != null) customerRunnable.cancel();
        customerRunnable = null;
        if(timerRunnable != null) timerRunnable.cancel();
        timerRunnable = null;
        redScore = 0;
        blueScore = 0;

        redTables.clear();
        blueTables.clear();

        redCustomers.clear();
        blueCustomers.clear();

        RED.forEach(player -> player.removePotionEffect(PotionEffectType.SPEED));

        BLUE.forEach(player -> player.removePotionEffect(PotionEffectType.SPEED));

        Utilities.endGameResuable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) redSpawn.get("x")).doubleValue(), ((Number) redSpawn.get("y")).doubleValue(), ((Number) redSpawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of cooking chaos is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
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
            MinigameFlag.DISABLE_FALL_DAMAGE,
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.CANNOT_TRAMPLE_FARMLAND
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = (String) mapData.get("worldName");
        Map<String, Object> redSpawn = (Map<String, Object>) mapData.get("redSpawn");
        Map<String, Object> blueSpawn = (Map<String, Object>) mapData.get("blueSpawn");
        World world = Bukkit.getWorld(worldName);

        if(RED.contains(player)){
            Kits.kitPlayer(Kits.cookingchaos_kit, player, Material.RED_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)redSpawn.get("x")).doubleValue(), ((Number)redSpawn.get("y")).doubleValue(), ((Number)redSpawn.get("z")).doubleValue()));
        } else if(BLUE.contains(player)){
            Kits.kitPlayer(Kits.cookingchaos_kit, player, Material.BLUE_CONCRETE);
            event.setRespawnLocation(new Location(world, ((Number)blueSpawn.get("x")).doubleValue(), ((Number)blueSpawn.get("y")).doubleValue(), ((Number)blueSpawn.get("z")).doubleValue()));
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 4, false, false, false));
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {

    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.mergeScoreboards(
                        CMScoreboardManager.scoreboards.get("cookingchaos").getScoreboard(player),
                        scoreboard
                )
        );
    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of(
            StarSource.KILL, 2,
            StarSource.WIN, 20
        );
    }

    @Override
    public String getId() {
        return "cookingchaos";
    }

    @Override
    public String getName() {
        return "Cooking Chaos";
    }

    @Override
    public String getDescription() {
        return "Race for resources to cook for your animal patreons where pvp is enabled, you can sabotage the other team or play it safe and get your own resources. The team with the most customers fed in 10 minutes wins. ";
    }
}
