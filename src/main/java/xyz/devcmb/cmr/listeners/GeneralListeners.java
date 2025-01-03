package xyz.devcmb.cmr.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devcmb.cmr.CmbMinigamesRandom;
import xyz.devcmb.cmr.utils.Colors;
import xyz.devcmb.cmr.utils.Format;

import java.lang.reflect.Field;
import java.util.List;

/**
 * A class for general listeners that apply all over the server
 */
public class GeneralListeners implements Listener {
    private CommandMap commandMap;
    private final List<String> blockedCharacters = List.of(
        "\uE00E",
        "⭐",
        "\uE003",
        "\uE004",
        "\uE005",
        "\uE006",
        "\uE007",
        "\uE008",
        "\uE009",
        "\uE00A",
        "\uE00B",
        "\uE00C",
        "\u1F46",
        "ὕ",
        "\uE000",
        "\uE001",
        "\uE002",
        "\uE00D"
    );

    public GeneralListeners() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            CmbMinigamesRandom.LOGGER.severe("Failed to get command map.");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command);
        Command minecraftCommand = commandMap.getCommand(command);

        if (pluginCommand == null && minecraftCommand == null) {
            Component message = Component.text("❓ ").append(Component.text("This command does not exist.").color(Colors.RED));

            event.getPlayer().sendMessage(message);
            event.setCancelled(true);
        } else if(command.equals("stop") || command.equals("reload")) {
            Component message = Component.text("❓ ").append(Component.text("No.").color(Colors.RED));

            event.getPlayer().sendMessage(message);
            event.setCancelled(true);
        } else if (pluginCommand != null) {
            String permission = pluginCommand.getPermission();
            if (permission != null && !event.getPlayer().hasPermission(permission)) {
                Component message = Component.text("❓ ").append(Component.text("You do not have permission to use this command.").color(Colors.RED));

                event.getPlayer().sendMessage(message);
                event.setCancelled(true);
            }
        } else {
            String permission = minecraftCommand.getPermission();
            if (permission != null && !event.getPlayer().hasPermission(permission)) {
                Component message = Component.text("❓ ").append(Component.text("You do not have permission to use this command.").color(Colors.RED));
                event.getPlayer().sendMessage(message);
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        if(event.getMessage().contains("https://")){
            Component text = Component.text("❓ ").append(Component.text("You cannot send links in chat.").color(Colors.RED));
            event.getPlayer().sendMessage(text);
            event.setCancelled(true);
            return;
        }

        for (String blockedCharacter : blockedCharacters) {
            if (event.getMessage().contains(blockedCharacter)) {
                Component text = Component.text("❓ ").append(Component.text("Your message contains a blocked character.").color(Colors.RED));

                event.getPlayer().sendMessage(text);
                event.setCancelled(true);
                return;
            }
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setFormat(LegacyComponentSerializer.legacySection().serialize(Component.text(Format.formatPlayerName(player)).append(Component.text(": ")).append(Component.text(message)).color(Colors.WHITE)));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            }, 3); // This is required to prevent the unlimited blocks from giving you it back
        }
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            Bukkit.getScheduler().runTaskLater(CmbMinigamesRandom.getPlugin(), () -> {
                ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
                ItemStack itemInOffhand = event.getPlayer().getInventory().getItemInOffHand();
                if (itemInHand.getType() == Material.GLASS_BOTTLE) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }

                if (itemInOffhand.getType() == Material.GLASS_BOTTLE) {
                    itemInOffhand.setAmount(itemInOffhand.getAmount() - 1);
                }
            }, 1L);
        }
    }
}
