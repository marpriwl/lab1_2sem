package service;

import domain.Sample;
import domain.SampleStatus;
import storage.DbStorage;
import validation.SampleValidator;

import java.util.TreeMap;
import java.util.stream.Collectors;

public class SampleService {
    private final TreeMap<Long, Sample> samples = new TreeMap<>();
    private final DbStorage dbStorage;

    public SampleService() {
        this(null);
    }

    public SampleService(DbStorage dbStorage) {
        this.dbStorage = dbStorage;
    }

    public long add(String name, String type, String location, long ownerId) {
        SampleValidator.validate(name, type, location);

        long id = dbStorage == null ? IdGenerator.nextSampleId() : 0;
        Sample sample = new Sample(id, name, type, location, SampleStatus.ACTIVE, ownerId);

        if (dbStorage != null) {
            sample = dbStorage.insertSample(sample);
            id = sample.getId();
        }

        samples.put(id, sample);
        return id;
    }

    public Sample getById(long id) {
        Sample sample = samples.get(id);

        if (sample == null) {
            throw new IllegalArgumentException("Sample id=" + id + " was not found");
        }

        return sample;
    }

    public String list(String statusFilter) {
        var stream = samples.values().stream();

        if (statusFilter != null) {
            SampleStatus status = SampleStatus.valueOf(statusFilter.toUpperCase());
            stream = stream.filter(sample -> sample.getStatus() == status);
        }

        var result = stream.toList();

        if (result.isEmpty()) {
            return "No samples";
        }

        return result.stream()
                .map(sample -> String.format(
                        "%-4d %-20s %-10s %-15s %-10s owner=%d",
                        sample.getId(),
                        sample.getName(),
                        sample.getType(),
                        sample.getLocation(),
                        sample.getStatus(),
                        sample.getOwnerId()
                ))
                .collect(Collectors.joining("\n"));
    }

    public void update(long id, String field, String value) {
        update(id, field, value, -1);
    }

    public void update(long id, String field, String value, long actorId) {
        Sample sample = getById(id);
        ensureOwner(sample, actorId);

        switch (field.toLowerCase()) {
            case "name" -> {
                SampleValidator.validate(value, sample.getType(), sample.getLocation());
                sample.setName(value);
            }
            case "type" -> {
                SampleValidator.validate(sample.getName(), value, sample.getLocation());
                sample.setType(value);
            }
            case "location" -> {
                SampleValidator.validate(sample.getName(), sample.getType(), value);
                sample.setLocation(value);
            }
            case "status" -> {
                SampleValidator.validateStatus(value);
                sample.setStatus(SampleStatus.valueOf(value.toUpperCase()));
            }
            default -> throw new IllegalArgumentException("Cannot update field '" + field + "'");
        }

        sample.updateTimestamp();

        if (dbStorage != null) {
            dbStorage.updateSample(sample);
        }
    }

    public void archive(long id) {
        archive(id, -1);
    }

    public void archive(long id, long actorId) {
        Sample sample = getById(id);
        ensureOwner(sample, actorId);

        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new IllegalArgumentException("Sample is already archived");
        }

        sample.setStatus(SampleStatus.ARCHIVED);
        sample.updateTimestamp();

        if (dbStorage != null) {
            dbStorage.updateSample(sample);
        }
    }

    public void delete(long id) {
        samples.remove(id);
        if (dbStorage != null) {
            dbStorage.deleteSample(id);
        }
    }

    public void restore(Sample sample) {
        samples.put(sample.getId(), sample);
        if (dbStorage != null) {
            dbStorage.insertSample(sample);
        }
    }

    public void replaceAll(TreeMap<Long, Sample> loadedSamples) {
        samples.clear();
        samples.putAll(loadedSamples);

        long nextId = samples.isEmpty()
                ? 1
                : samples.lastKey() + 1;

        IdGenerator.setSampleId(nextId);
    }

    public void refreshFromDatabase() {
        if (dbStorage == null) {
            return;
        }

        TreeMap<Long, Sample> loadedSamples = dbStorage.loadLabData()
                .getSamples()
                .stream()
                .collect(Collectors.toMap(
                        Sample::getId,
                        sample -> sample,
                        (left, right) -> left,
                        TreeMap::new
                ));

        replaceAll(loadedSamples);
    }

    public TreeMap<Long, Sample> getAll() {
        return samples;
    }

    private void ensureOwner(Sample sample, long actorId) {
        if (actorId > 0 && sample.getOwnerId() != actorId) {
            throw new IllegalStateException("Error: you cannot change another user's sample");
        }
    }
}
