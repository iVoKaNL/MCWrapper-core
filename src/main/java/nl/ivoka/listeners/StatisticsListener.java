package nl.ivoka.listeners;

import nl.ivoka.MCWrapper;
import nl.ivoka.mongo.data.entity.PlayerStatistics;
import nl.ivoka.mongo.repository.PlayerStatisticsRepository;
import nl.ivoka.task.TaskScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class StatisticsListener implements Listener, TaskScheduler<MCWrapper> {

    private MCWrapper plugin;

    public StatisticsListener(MCWrapper plugin) {
        this.plugin = plugin;
    }

    public MCWrapper getPlugin() {
        return plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        runAsync(() -> {
            PlayerStatisticsRepository repository = plugin.getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
            PlayerStatistics entity = repository.read(player.getUniqueId(), true);

            entity.setLogins(entity.getLogins() + 1);
            repository.save(entity);
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        runAsync(() -> {
            PlayerStatisticsRepository repository = plugin.getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
            PlayerStatistics entity = repository.read(player.getUniqueId(), true);

            entity.setDeaths(entity.getDeaths() + 1);
            repository.save(entity);

            if (killer != null) {
                PlayerStatistics killerEntity = repository.read(killer.getUniqueId(), true);

                killerEntity.setKills(killerEntity.getKills() + 1);
                repository.save(killerEntity);
            }
        });
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        runAsync(() -> {
            PlayerStatisticsRepository repository = plugin.getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
            PlayerStatistics entity = repository.read(player.getUniqueId(), true);

            entity.setMessagesSent(entity.getMessagesSent() + 1);
            repository.save(entity);
        });
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        runAsync(() -> {
            PlayerStatisticsRepository repository = plugin.getMongoDataSource().getRepository(PlayerStatisticsRepository.class);
            PlayerStatistics entity = repository.read(player.getUniqueId(), true);

            entity.setTeleports(entity.getTeleports() + 1);
            repository.save(entity);
        });
    }
}