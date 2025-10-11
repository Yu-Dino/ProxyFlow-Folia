package net.yuflow.proxyflow;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyFlowFolia extends JavaPlugin {

    private ConfigManager configManager;
    private final Queue<UUID> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Set<UUID> allowedToJoin = ConcurrentHashMap.newKeySet();
    private double joinAllowance = 0.0;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this.getLogger(), this.getDataFolder().toPath());

        getCommand("proxyflow").setExecutor(new ProxyFlowCommand(this.configManager));
        getCommand("maintenance").setExecutor(new MaintenanceCommand(this.configManager, this));

        getServer().getPluginManager().registerEvents(new ConnectionListener(this.getLogger(), this.configManager), this);
        getServer().getPluginManager().registerEvents(new MaintenanceListener(this.configManager), this);

        if (this.configManager.isQueueEnabled()) {
            getLogger().info("Warteschlange wird aktiviert...");
            getServer().getPluginManager().registerEvents(new QueueListener(this), this);
            startQueueTask();
            getLogger().info("Warteschlange erfolgreich aktiviert.");
        }

        getLogger().info("                                                                                                                             ==   *+ +++*+*+**#%     ");
        getLogger().info("                                                                                                                          =-==  **=====++++*%#%      ");
        getLogger().info("                                                                                                                      = =--+-=@**=====+++*%#%%       ");
        getLogger().info("                                                                                                                     ------==+%*+==+++*+**##%        ");
        getLogger().info(" _______  ______    _______  __   __  __   __  _______  ___      _______  _     _    __   __  ____                  =-+--====*#+==++*#***##%         ");
        getLogger().info("|       ||    _ |  |       ||  |_|  ||  | |  ||       ||   |    |       || | _ | |  |  | |  ||    |              = ==-=-=+=+*#++++*#++*%@@@          ");
        getLogger().info("|    _  ||   | ||  |   _   ||       ||  |_|  ||    ___||   |    |   _   || || || |  |  |_|  | |   |             += ====-+==+*#++++++***#%%@%         ");
        getLogger().info("|   |_| ||   |_||_ |  | |  ||       ||       ||   |___ |   |    |  | |  ||       |  |       | |   |             +====+*#==+++#+++**###%%%%           ");
        getLogger().info("|    ___||    __  ||  |_|  | |     | |_     _||    ___||   |___ |  |_|  ||       |  |       | |   |            +++=++##%+++#%+++*##%%%               ");
        getLogger().info("|   |    |   |  | ||       ||   _   |  |   |  |   |    |       ||       ||   _   |   |     |  |   |            #*#+*##**++***##+****##%%%            ");
        getLogger().info("|___|    |___|  |_||_______||__| |__|  |___|  |___|    |_______||_______||__| |__|    |___|   |___|            @%@*##*#**##%%%%%@%%%%%@              ");
        getLogger().info("                                                                                                                @@%%#%**#%%%@%%%@@@@@@@              ");
        getLogger().info("                      ProxyFlow v1.1.2-Folia powered by Yu_Dino                                                  %*%###%%%%@@@@@@@@                  ");
        getLogger().info("                      Plugin has been initialized successfully!                                                 *@@%%%%%%#%%%%@@@                    ");
        getLogger().info("                                                                                                              +     @@@@@@@@                         ");
        getLogger().info("                                                                                                          +                                          ");
    }

    private void startQueueTask() {
        GlobalRegionScheduler scheduler = getServer().getGlobalRegionScheduler();

        scheduler.runAtFixedRate(this, scheduledTask -> {
            if (!configManager.isQueueEnabled() || waitingQueue.isEmpty()) {
                this.joinAllowance = 0;
                return;
            }

            int joinsPerSecond = configManager.getJoinsPerSecond();
            if (joinsPerSecond <= 0) {
                return;
            }

            double joinsPerTick = joinsPerSecond / 20.0;
            this.joinAllowance += joinsPerTick;

            if (this.joinAllowance < 1.0) {
                return;
            }

            int onlinePlayers = getServer().getOnlinePlayers().size();
            int maxPlayers = getServer().getMaxPlayers();
            int freeSlots = maxPlayers - onlinePlayers;

            int playersToLetIn = (int) this.joinAllowance;
            playersToLetIn = Math.min(playersToLetIn, freeSlots);
            playersToLetIn = Math.min(playersToLetIn, waitingQueue.size());

            for (int i = 0; i < playersToLetIn; i++) {
                UUID uuidToAllow = waitingQueue.poll();
                if (uuidToAllow != null) {
                    allowedToJoin.add(uuidToAllow);
                } else {
                    break;
                }
            }
            this.joinAllowance -= playersToLetIn;
        }, 1L, 1L);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Queue<UUID> getWaitingQueue() {
        return this.waitingQueue;
    }

    public Set<UUID> getAllowedToJoin() {
        return this.allowedToJoin;
    }
}