package nl.ivoka.mongo.repository;

import nl.ivoka.mongo.data.entity.PlayerStatistics;
import org.mongodb.morphia.Datastore;

import java.util.UUID;

public class PlayerStatisticsRepository extends Repository<PlayerStatistics> {

    public PlayerStatisticsRepository(Datastore datastore) {
        super(datastore, PlayerStatistics.class);
    }

    public PlayerStatistics read(UUID uuid) {
        return read(uuid, false);
    }

    public PlayerStatistics read(UUID uuid, boolean createIfNotExists) {
        PlayerStatistics result = datastore.createQuery(PlayerStatistics.class)
                .field("uuid").equal(uuid)
                .get();

        if (result == null && createIfNotExists) {
            result = new PlayerStatistics(uuid);

            datastore.save(result);
        }

        return result;
    }
}