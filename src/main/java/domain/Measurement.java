package domain;

import java.time.Instant;

public final class Measurement {
    private long id;
    private long sampleId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private String method;
    private Instant measuredAt;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Measurement(long id, long sampleId, MeasurementParam param, double value,
                       String unit, String method,  String ownerUsername) {
        this.id = id;
        this.sampleId = sampleId;
        this.param = param;
        this.value = value;
        this.unit = unit;
        this.method = method;
        this.measuredAt = Instant.now();
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    public long getId() { return id; }
    public long getSampleId() { return sampleId; }
    public MeasurementParam getParam() { return param; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getMethod() { return method; }
    public Instant getMeasuredAt() { return measuredAt; }
    public String getOwnerUsername() { return ownerUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void updateTimestamp() { this.updatedAt = Instant.now(); }
}
