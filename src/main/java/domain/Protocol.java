package domain;

import java.time.Instant;
import java.util.Set;

public final class Protocol {
    private long id;
    private String name;
    private Set<MeasurementParam> requiredParams;
    private long ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    public Protocol() {
    }

    public Protocol(long id, String name, Set<MeasurementParam> requiredParams, long ownerId) {
        this.id = id;
        this.name = name;
        this.requiredParams = requiredParams;
        this.ownerId = ownerId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public Set<MeasurementParam> getRequiredParams() { return requiredParams; }
    public long getOwnerId() { return ownerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void updateTimestamp() { this.updatedAt = Instant.now(); }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredParams(Set<MeasurementParam> requiredParams) {
        this.requiredParams = requiredParams;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}

