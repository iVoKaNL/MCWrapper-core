package nl.ivoka.mongo.data.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.UUID;

@Entity("statistics")
@Indexes(
        @Index(value = "uuid", fields = @Field("uuid"))
)
public class PlayerStatistics {

    @Id
    private ObjectId id;

    private UUID uuid;
    private int kills;
    private int deaths;
    private int messagesSent;
    private int logins;
    private int teleports;

    public PlayerStatistics() {} // Empty constructor required by Morphia

    public PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
    }

    // region Getters & setters
    public ObjectId getId() {
        return id;
    }
    public UUID getUuid() {
        return uuid;
    }

    public int getKills() { return kills; }
    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getMessagesSent() {
        return messagesSent;
    }
    public void setMessagesSent(int messagesSent) {
        this.messagesSent = messagesSent;
    }

    public int getLogins() {
        return logins;
    }
    public void setLogins(int logins) {
        this.logins = logins;
    }

    public int getTeleports() {
        return teleports;
    }
    public void setTeleports(int teleports) {
        this.teleports = teleports;
    }
    // endregion
}