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
                sender.sendMessage(Component.text("Du hast keine Berechtigung für diesen Befehl.", NamedTextColor.RED));
                return true;
            }

            this.configManager.loadConfig();
            sender.sendMessage(Component.text("ProxyFlow Konfiguration wurde neu geladen!", NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Verwendung: /proxyflow reload", NamedTextColor.GRAY));
        return true;
    }
}