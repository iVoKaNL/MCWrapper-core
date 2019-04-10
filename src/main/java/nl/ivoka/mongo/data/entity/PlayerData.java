package nl.ivoka.mongo.data.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.UUID;

@Entity("players")
@Indexes({
        @Index(value = "uuid", fields = @Field("uuid")),
        @Index(value = "username", fields = @Field("username"))
})
public class PlayerData {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String username;

    public PlayerData() {} // Empty constructor required by Morphia

    public PlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    // region Getters & setters
    public ObjectId getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
    // endregion
}