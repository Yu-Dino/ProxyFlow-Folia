package net.yuflow.proxyflow;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MaintenanceListener implements Listener {

    private final ConfigManager configManager;

    public MaintenanceListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onServerListPing(PaperServerListPingEvent event) {
        if (configManager.isMaintenanceEnabled()) {
            event.motd(LegacyComponentSerializer.legacyAmpersand().deserialize(configManager.getMaintenanceMotd()));
            event.setVersion("Wartung");
            event.setProtocolVersion(-1);
            event.setHidePlayers(true);
        }
    }
}