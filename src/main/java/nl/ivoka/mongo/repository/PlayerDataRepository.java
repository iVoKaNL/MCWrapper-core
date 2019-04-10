package nl.ivoka.mongo.repository;

import nl.ivoka.mongo.data.entity.PlayerData;
import org.mongodb.morphia.Datastore;

import java.util.UUID;

public class PlayerDataRepository extends Repository<PlayerData> {

    public PlayerDataRepository(Datastore datastore) {
        super(datastore, PlayerData.class);
    }

    public PlayerData read(UUID uuid) {
        return  datastore.createQuery(PlayerData.class)
                .field("uuid").equal(uuid)
                .get();
    }
}