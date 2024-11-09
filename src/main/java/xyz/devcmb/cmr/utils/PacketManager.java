package xyz.devcmb.cmr.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PacketManager {
    private final ProtocolManager protocolManager;
    private final Plugin plugin;
    private final Map<String, Set<UUID>> glowingGroups = new HashMap<>();

    public PacketManager(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerPacketListener();
    }

    public void giveGlow(String groupName, List<Player> players) {
        glowingGroups.putIfAbsent(groupName, new HashSet<>());
        Set<UUID> group = glowingGroups.get(groupName);
        for (Player player : players) {
            group.add(player.getUniqueId());
        }
    }

    public void removeGlow(String groupName, List<Player> players) {
        Set<UUID> group = glowingGroups.get(groupName);
        if (group != null) {
            for (Player player : players) {
                group.remove(player.getUniqueId());
            }
        }
    }

    public void removeGroup(String groupName){
        glowingGroups.remove(groupName);
    }

    private void registerPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player receiver = event.getPlayer();
                Entity entity = event.getPacket().getEntityModifier(event).read(0);

                if (entity instanceof Player targetPlayer) {
                    String receiverGroup = getGroupForPlayer(receiver);
                    String targetGroup = getGroupForPlayer(targetPlayer);

                    if (receiverGroup != null && receiverGroup.equals(targetGroup) || receiver.equals(targetPlayer)) {
                        WrappedDataWatcher watcher = new WrappedDataWatcher(event.getPacket().getWatchableCollectionModifier().read(0));
                        WrappedDataWatcher.WrappedDataWatcherObject glowing = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
                        watcher.setObject(glowing, (byte) (0x40));
                        event.getPacket().getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
                    }
                }
            }
        });
    }

    private String getGroupForPlayer(Player player) {
        if(!player.isOnline()) return null;
        for (Map.Entry<String, Set<UUID>> entry : glowingGroups.entrySet()) {
            if (entry.getValue().contains(player.getUniqueId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void cleanup() {
        glowingGroups.clear();
        protocolManager.removePacketListeners(plugin);
    }
}
