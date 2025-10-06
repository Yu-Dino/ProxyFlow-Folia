package net.yuflow.proxyflow;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ProxyFlowCommand implements CommandExecutor {
    private final ConfigManager configManager;

    public ProxyFlowCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("proxyflow.command.reload")) {
                sender.sendMessage(Component.text("You do not have permission for this command.", NamedTextColor.RED));
                return true;
            }

            this.configManager.loadConfig();
            sender.sendMessage(Component.text("ProxyFlow configuration has been reloaded!", NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Usage: /proxyflow reload", NamedTextColor.GRAY));
        return true;
    }
}