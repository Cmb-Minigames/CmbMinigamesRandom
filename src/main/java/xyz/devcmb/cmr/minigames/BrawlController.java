package xyz.devcmb.cmr.minigames;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.interfaces.scoreboards.CMScoreboardManager;
import xyz.devcmb.cmr.items.ItemManager;
import xyz.devcmb.cmr.minigames.bases.FFAMinigameBase;
import xyz.devcmb.cmr.utils.Kits;
import xyz.devcmb.cmr.utils.Utilities;
import xyz.devcmb.cmr.timers.Timer;
import xyz.devcmb.cmr.timers.TimerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The brawl minigame controller
 */
public class BrawlController extends FFAMinigameBase implements Minigame {
    private final List<ItemStack> smallChestItems = new ArrayList<>();
    private final List<ItemStack> largeChestItems = new ArrayList<>();
    public Timer timer;

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
        super.start();

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> players.forEach(player -> Utilities.Countdown(player, 10)), 20 * 2);

        Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
            Map<?, List<?>> kit = Kits.brawl_kit;
            players.forEach(player -> {
                Kits.kitPlayer(kit, player, Material.WHITE_CONCRETE);
                player.setSaturation(0);
                Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(40);
                player.setHealth(40);
            });

            timer = TimerManager.runTimer("brawl");

            // WHAT IS THIS
            // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
            if(mapData == null) return; // early ending nulls mapData
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
        }, 20 * 12);
    }

    @Override
    public void stop() {
        timer = null;
        allPlayers.forEach(player -> {
            Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
            player.setHealth(20);
        });
        super.stop();
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
                players.add(event.getEntity()); // this will end the game with the only player as the winner
            }
        }

        if(players.size() == 1){
            timer.end();
            endGame();
        } else if(players.size() == 2){
            allPlayers.forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 10, 1);
                player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "STANDOFF", "", 15, 50, 10);
            });
        }
    }

    @Override
    protected void endGame() {
        timer = null;
        super.endGame();
    }

    @Override
    public void updateScoreboard(Player player) {
        CMScoreboardManager.sendScoreboardAlongDefaults(
                player,
                CMScoreboardManager.scoreboards.get("brawl").getScoreboard(player)
        );
    }

    @Override
    public Map<StarSource, Integer> getStarSources() {
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
