package net.yuflow.proxyflow;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueListener implements Listener {

    private final ProxyFlowFolia plugin;

    public QueueListener(ProxyFlowFolia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!plugin.getConfigManager().isQueueEnabled()) {
            return;
        }

        UUID playerUuid = event.getPlayer().getUniqueId();

        if (event.getPlayer().hasPermission("proxyflow.queue.bypass")) {
            return;
        }

        if (plugin.getAllowedToJoin().remove(playerUuid)) {
            return;
        }

        if (!plugin.getWaitingQueue().contains(playerUuid)) {
            plugin.getWaitingQueue().add(playerUuid);
        }

        String message = plugin.getConfigManager().getQueueKickMessage();
        message = message.replace("{position}", String.valueOf(getQueuePosition(playerUuid)));
        message = message.replace("{total}", String.valueOf(plugin.getWaitingQueue().size()));
        message = ChatColor.translateAlternateColorCodes('&', message);

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
    }

    private int getQueuePosition(UUID uuid) {
        AtomicInteger position = new AtomicInteger(0);
        for (UUID entry : plugin.getWaitingQueue()) {
            position.incrementAndGet();
            if (entry.equals(uuid)) {
                return position.get();
            }
        }
        return 0;
    }
}