package nl.ivoka.listeners;

import nl.ivoka.MCWrapper;
import nl.ivoka.mongo.data.entity.PlayerData;
import nl.ivoka.mongo.repository.PlayerDataRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class DataListener implements Listener {

    private MCWrapper plugin;

    public DataListener(MCWrapper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String username = event.getName();

        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            PlayerDataRepository repository = plugin.getMongoDataSource().getRepository(PlayerDataRepository.class);
            PlayerData data = repository.read(uuid);
            if (data == null)
                data = new PlayerData(uuid, username);

            data.setUsername(username);
            repository.save(data);
        }
    }
}