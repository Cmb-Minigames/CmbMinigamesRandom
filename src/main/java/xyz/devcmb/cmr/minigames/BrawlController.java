package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.GameManager;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.items.ItemManager;
import xyz.devcmb.cmr.utils.Database;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.MapLoader;
import xyz.devcmb.cmr.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BrawlController implements Minigame {
    public List<Player> players = new ArrayList<>();
    public List<Player> allPlayers = new ArrayList<>();
    private Location spawnLocation = null;
    private final List<ItemStack> smallChestItems = new ArrayList<>();
    private final List<ItemStack> largeChestItems = new ArrayList<>();

    public BrawlController(){
        // Small items
        smallChestItems.add(new ItemStack(Material.IRON_SWORD));
        smallChestItems.add(new ItemStack(Material.STONE_AXE));
        smallChestItems.add(new ItemStack(Material.IRON_BOOTS));
        smallChestItems.add(new ItemStack(Material.IRON_LEGGINGS));
        smallChestItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        smallChestItems.add(new ItemStack(Material.WIND_CHARGE, 2));
        smallChestItems.add(new ItemStack(Material.ENDER_PEARL));
        smallChestItems.add(new ItemStack(Material.ARROW, 3));
        smallChestItems.add(new ItemStack(Material.CROSSBOW));
        smallChestItems.add(new ItemStack(Material.SNOWBALL, 16));
        smallChestItems.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        smallChestItems.add(new ItemStack(Material.TNT));

        ItemStack harmingPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta harmingMeta = (PotionMeta) harmingPotion.getItemMeta();
        if(harmingMeta == null) return;
        harmingMeta.setBasePotionType(PotionType.HARMING);
        harmingMeta.setItemName("Splash Potion of Harming");
        harmingPotion.setItemMeta(harmingMeta);

        smallChestItems.add(harmingPotion);

        ItemStack poisonPotion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta poisonMeta = (PotionMeta) poisonPotion.getItemMeta();
        if(poisonMeta == null) return;
        poisonMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 8 * 20, 1), true);
        poisonMeta.setItemName("Splash Potion of Poison");
        poisonPotion.setItemMeta(poisonMeta);

        smallChestItems.add(poisonPotion);
        smallChestItems.add(ItemManager.items.get("Fireball"));

        // Large items
        largeChestItems.addAll(smallChestItems);
        largeChestItems.add(new ItemStack(Material.DIAMOND_SWORD));
        largeChestItems.add(new ItemStack(Material.DIAMOND_AXE));
        largeChestItems.add(new ItemStack(Material.DIAMOND_BOOTS));
        largeChestItems.add(new ItemStack(Material.DIAMOND_LEGGINGS));
        largeChestItems.add(new ItemStack(Material.IRON_CHESTPLATE));
        largeChestItems.add(new ItemStack(Material.DIAMOND_HELMET));

        ItemStack harmingPotion2 = new ItemStack(Material.SPLASH_POTION);
        PotionMeta harmingMeta2 = (PotionMeta) harmingPotion2.getItemMeta();
        if(harmingMeta2 == null) return;
        harmingMeta2.setBasePotionType(PotionType.STRONG_HARMING);
        harmingMeta2.setItemName("Splash Potion of Harming II");
        harmingPotion2.setItemMeta(harmingMeta2);

        largeChestItems.add(harmingPotion2);

        ItemStack poisonPotion2 = new ItemStack(Material.SPLASH_POTION);
        PotionMeta poisonMeta2 = (PotionMeta) poisonPotion2.getItemMeta();
        if(poisonMeta2 == null) return;
        poisonMeta2.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 2), true);
        poisonMeta2.setItemName("Splash Potion of Poison II");
        poisonPotion2.setItemMeta(poisonMeta2);

        largeChestItems.add(poisonPotion2);
        largeChestItems.add(ItemManager.items.get("Meteor Shower"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        Utilities.gameStartReusable();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        if (mapData == null) {
            CmbMinigamesRandom.LOGGER.warning("MapData is not defined.");
            return;
        }

        players.addAll(Bukkit.getOnlinePlayers());
        allPlayers.addAll(Bukkit.getOnlinePlayers());

        String worldName = MapLoader.LOADED_MAP;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            CmbMinigamesRandom.LOGGER.warning("World " + worldName + " is not loaded.");
            return;
        }

        spawnLocation = Utilities.getLocationFromConfig(mapData, world, "spawn");

        players.forEach(player -> {
            player.teleport(spawnLocation);
            player.setSaturation(20);
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            Utilities.Countdown(player, 10);
        });

        // REMEMBER
        // use runTaskLater from the getScheduler method of the Bukkit class instead of new BukkitRunnable
        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Map<?, List<?>> kit = Kits.brawl_kit;
            players.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                player.setSaturation(0);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40);
                player.setHealth(40);
            });


            // WHAT IS THIS
            // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            List<Map<String, Map<String, Number>>> largeChests = (((Map<String, List<Map<String, Map<String,Number>>>>)mapData.get("chests")).get("large"));
            List<Map<String, Map<String, Number>>> smallChests = (((Map<String, List<Map<String, Map<String, Number>>>>)mapData.get("chests")).get("small"));

            largeChests.forEach(chest -> {
                Map<String, Number> location = chest.get("location");

                Location loc = new Location(
                        world,
                        location.get("x").doubleValue(),
                        location.get("y").doubleValue(),
                        location.get("z").doubleValue()
                );

                Block chest1 = loc.getBlock();
                if (chest1.getState() instanceof Chest chestData) {
                    Chest data = Utilities.fillChestRandomly(chestData, largeChestItems, 5, 9);
                    chest1.setBlockData(data.getBlockData());
                } else {
                    CmbMinigamesRandom.LOGGER.warning("Block at " + loc + " is not a chest.");
                }
            });

            smallChests.forEach(chest -> {
                Map<String, Number> location = chest.get("location");
                Location loc = new Location(
                        world,
                        location.get("x").doubleValue(),
                        location.get("y").doubleValue(),
                        location.get("z").doubleValue()
                );

                Block chest1 = loc.getBlock();
                if (chest1.getState() instanceof Chest chestData) {
                    Chest data = Utilities.fillChestRandomly(chestData, smallChestItems, 3, 7);
                    chest1.setBlockData(data.getBlockData());
                } else {
                    CmbMinigamesRandom.LOGGER.warning("Block at " + loc + " is not a chest.");
                }
            });
        }, 20 * 10);
    }

    @Override
    public void stop() {
        players.clear();
        allPlayers.clear();
        spawnLocation = null;

        Utilities.endGameResuable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> mapData = (Map<String, Object>) GameManager.currentMap.get("map");
        String worldName = MapLoader.LOADED_MAP;
        Map<String, Object> spawn = (Map<String, Object>) mapData.get("spawn");

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            player.teleport(new Location(Bukkit.getWorld(worldName), ((Number) spawn.get("x")).doubleValue(), ((Number) spawn.get("y")).doubleValue(), ((Number) spawn.get("z")).doubleValue()));
            player.sendMessage(ChatColor.RED + "A game of Brawl is currently active, and you have been added as a spectator.");
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 10L);
        }, 10L);
    }

    @Override
    public Number playerLeave(Player player) {
        players.remove(player);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);

        if(CmbMinigamesRandom.DeveloperMode){
            return (players.isEmpty()) ? 0 : null;
        } else {
            if(players.size() == 1){
                Player winner = players.getFirst();
                Database.addUserStars(winner, getStarSources().get(StarSource.WIN).intValue());
                winner.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
                winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                winner.getInventory().clear();
                winner.setGameMode(GameMode.SPECTATOR);
            } else if(players.isEmpty()){
                return 0;
            }
        }

        return null;
    }

    private void endGame(){
        GameManager.gameEnding = true;

        Player winner = players.getFirst();
        Database.addUserStars(winner, getStarSources().get(StarSource.WIN).intValue());
        winner.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", "", 5, 80, 10);
        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
        winner.getInventory().clear();
        winner.setGameMode(GameMode.SPECTATOR);

        allPlayers.forEach(player -> {
            if(player != winner){
                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", "", 5, 80, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            }
        });

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), this::stop, 20 * 8);
    }

    @Override
    public List<MinigameFlag> getFlags() {
        return List.of(
            MinigameFlag.DISPLAY_KILLER_IN_DEATH_MESSAGE,
            MinigameFlag.DISABLE_BLOCK_DROPS,
            MinigameFlag.UNLIMITED_BLOCKS,
            MinigameFlag.NO_REPEATED_TOOLS
        );
    }

    @Override
    public void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(spawnLocation == null) return;
        event.setRespawnLocation(spawnLocation);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void playerDeath(PlayerDeathEvent event) {
        players.remove(event.getEntity());
        if(CmbMinigamesRandom.DeveloperMode){
            if(players.isEmpty()){
                players.add(event.getEntity());
                // this will end the game with the only player as the winner
            }
        }

        if(players.size() == 1){
            endGame();
        } else if(players.size() == 2){
            allPlayers.forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 10, 1);
                player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "STANDOFF", "", 15, 50, 10);
            });
        }
    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("brawl").getScoreboard(player)
        );
    }

    @Override
    public Map<StarSource, Number> getStarSources() {
        return Map.of(
            StarSource.KILL, 5,
            StarSource.WIN, 30
        );
    }

    @Override
    public String getId() {
        return "brawl";
    }

    @Override
    public String getName() {
        return "Brawl";
    }

    @Override
    public String getDescription() {
        return "A FFA gamemode where the last person standing wins. Custom items will be found randomly around the map in different chests, with each player having a cooldown on opening them, allowing you to open every 10 seconds.";
    }
}
