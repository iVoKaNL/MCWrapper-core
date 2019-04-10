package nl.ivoka.mongo.repository;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public abstract class Repository<T> {

    protected Datastore datastore;

    private Class<T> clazz;

    public Repository(Datastore datastore, Class<T> clazz) {
        this.datastore = datastore;
        this.clazz = clazz;
    }

    public Key<T> save(T object) {
        return datastore.save(object);
    }

    public T read(ObjectId id) {
        return datastore.get(clazz, id);
    }

    public UpdateResults update(T object, UpdateOperations<T> operations) {
        return datastore.update(object, operations);
    }

    public WriteResult delete(T object) {
        return datastore.delete(object);
    }

    public UpdateOperations<T> createOperations() {
        return datastore.createUpdateOperations(clazz);
    }
}