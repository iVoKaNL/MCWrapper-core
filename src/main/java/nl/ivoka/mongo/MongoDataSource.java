package nl.ivoka.mongo;

import com.mongodb.MongoClient;
import nl.ivoka.mongo.repository.PlayerStatisticsRepository;
import nl.ivoka.mongo.repository.Repository;
import nl.ivoka.mongo.repository.PlayerDataRepository;
import org.bukkit.configuration.ConfigurationSection;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;

import java.util.ArrayList;
import java.util.List;

public class MongoDataSource {

    private Datastore datastore;

    private List<Repository> repositories = new ArrayList<>();

    public void initializeDatastore(ClassLoader classLoader, ConfigurationSection config) {
        Morphia morphia = new Morphia();

        morphia.mapPackage("nl.ivoka.mongo.data.entity");
        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return classLoader;
            }
        });

        datastore = morphia.createDatastore(new MongoClient(), config.getString("database"));
        datastore.ensureIndexes();
    }

    public void initializeRepositories() {
        repositories.add(new PlayerDataRepository(datastore));
        repositories.add(new PlayerStatisticsRepository(datastore));
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public <T extends Repository> T getRepository(Class<T> clazz) {
        Repository repository = repositories.stream()
                .filter(h -> h.getClass() == clazz)
                .findFirst()
                .orElse(null);

        return repository != null ? clazz.cast(repository) : null;
    }
}