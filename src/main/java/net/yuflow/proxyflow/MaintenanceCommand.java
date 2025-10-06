package net.yuflow.proxyflow;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final ProxyFlowFolia plugin;

    public MaintenanceCommand(ConfigManager configManager, ProxyFlowFolia plugin) {
        this.configManager = configManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean currentState = configManager.isMaintenanceEnabled();
        boolean newState = !currentState;

        configManager.setMaintenance(newState);

        if (newState) {
            sender.sendMessage(Component.text("Maintenance mode has been enabled.", NamedTextColor.YELLOW));

            Component kickMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(configManager.getMaintenanceKickMessage());
            String bypassPermission = configManager.getMaintenanceBypassPermission();

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!player.hasPermission(bypassPermission)) {
                    player.getScheduler().run(plugin, scheduledTask -> player.kick(kickMessage), null);
                }
            }
        } else {
            sender.sendMessage(Component.text("Maintenance mode has been disabled.", NamedTextColor.GREEN));
        }
        return true;
    }
}