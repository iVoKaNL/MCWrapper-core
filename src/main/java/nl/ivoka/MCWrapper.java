package nl.ivoka;

import nl.ivoka.commands.Stats;
import nl.ivoka.connector.Server;
import nl.ivoka.listeners.DataListener;
import nl.ivoka.listeners.EventListener;
import nl.ivoka.listeners.StatisticsListener;
import nl.ivoka.mongo.MongoDataSource;
import nl.ivoka.mongo.data.entity.PlayerStatistics;
import nl.ivoka.mongo.repository.PlayerStatisticsRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MCWrapper extends JavaPlugin {

    private MongoDataSource mongoDataSource;
    private Server server;

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();
        initializeMongo();

        server = new Server(this);

        System.out.println("Starting MCWrapper-core...");
    }

    public void registerCommands() {
        // Register commands
        this.getCommand("stats").setExecutor(new Stats(this));
    }

    public void registerEvents() {
        // Register listeners
        getServer().getPluginManager().registerEvents(new DataListener(this), this);
        getServer().getPluginManager().registerEvents(new StatisticsListener(this), this);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    private void initializeMongo() {
        // Initialize Mongo Database
        ConfigurationSection section = getConfig().getConfigurationSection("mongo");

        mongoDataSource = new MongoDataSource();
        mongoDataSource.initializeDatastore(getClassLoader(), section);
        mongoDataSource.initializeRepositories();
    }

    @Override
    public void onDisable() {
        // Fired when the server stops and disables all plugins
        server.stopServer();

        System.out.println("Stopping MCWrapper-core...");
    }

    public MongoDataSource getMongoDataSource() {
        return mongoDataSource;
    }
    public Server getConnectorServer() { return server; }

    // region Command handlers
    public String getPlayers() {
        String msg = "PLAYERS "+Bukkit.getOnlinePlayers().size()+" ";

        for (Player player : Bukkit.getOnlinePlayers())
            msg += player.getName()+":"+player.getUniqueId()+" ";

        return msg.trim();
    }
    public String getAllPlayers() {
        String msg = "ALLPLAYERS "+Bukkit.getOfflinePlayers().length+" ";

        for (OfflinePlayer player : Bukkit.getOfflinePlayers())
            msg += player.getName()+":"+player.getUniqueId()+" ";

        return msg.trim();
    }

    public String getStats(String playerUUID) {
        String msg = "STATS ";
        OfflinePlayer player = null;

        for (OfflinePlayer _player : Bukkit.getOfflinePlayers()) {
            if (_player.getUniqueId().toString().equals(playerUUID))
                player = _player;
        }

        if (player == null)
            msg = "ERROR 2 use GETALLPLAYERS to get all players that play(ed) on this server";
        else {
            PlayerStatisticsRepository repository = getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
            PlayerStatistics statistics = repository.read(player.getUniqueId(), true);

            msg += player.getUniqueId().toString()+" ";

            msg += "kills:"+statistics.getKills()+" ";
            msg += "deaths:"+statistics.getDeaths()+" ";
            msg += "logins:"+statistics.getLogins()+" ";
            msg += "teleports:"+statistics.getTeleports();
        }

        return msg;
    }
    // endregion
}
