package domain;

import java.time.Instant;
import java.util.Set;

public final class Protocol {
    public long id;
    public String name;
    public Set<MeasurementParam> requiredParams;
    public String ownerUsername;
    public Instant createdAt;
    public Instant updatedAt;

    public Protocol(long id, String name, Set<MeasurementParam> requiredParams, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.requiredParams = requiredParams;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public Set<MeasurementParam> getRequiredParams() { return requiredParams; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void updateTimestamp() { this.updatedAt = Instant.now(); }
}

