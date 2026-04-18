package service;

import domain.Sample;
import domain.SampleStatus;
import validation.SampleValidator;

import java.util.TreeMap;
import java.util.stream.Collectors;

public class SampleService {
    private final TreeMap<Long, Sample> samples = new TreeMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public long add(String name, String type, String location) {
        SampleValidator.validate(name, type, location);
        long id = idGenerator.nextId();
        Sample sample = new Sample(id, name, type, location, SampleStatus.ACTIVE, "SYSTEM");
        samples.put(id, sample);
        return id;
    }

    public Sample getById(long id) {
        Sample s = samples.get(id);
        if (s == null) throw new IllegalArgumentException("образец с id=" + id + " не найден");
        return s;
    }

    public String list(String statusFilter) {
        var stream = samples.values().stream();

        if (statusFilter != null) {
            SampleStatus st = SampleStatus.valueOf(statusFilter.toUpperCase());
            stream = stream.filter(s -> s.getStatus() == st);
        }

        var result = stream.toList();

        if (result.isEmpty()) {
            return "Нет образцов";
        }

        return result.stream()
                .map(s -> String.format("%-4d %-20s %-10s %-15s %s",
                        s.getId(), s.getName(), s.getType(), s.getLocation(), s.getStatus()))
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    public void update(long id, String field, String value) {
        Sample s = getById(id);
        switch (field.toLowerCase()) {
            case "name" -> { SampleValidator.validate(value, s.getType(), s.getLocation()); s.setName(value); }
            case "type" -> { SampleValidator.validate(s.getName(), value, s.getLocation()); s.setType(value); }
            case "location" -> { SampleValidator.validate(s.getName(), s.getType(), value); s.setLocation(value); }
            case "status" -> {
                SampleValidator.validateStatus(value);
                s.setStatus(SampleStatus.valueOf(value.toUpperCase()));
            }
            default -> throw new IllegalArgumentException("нельзя менять поле '" + field + "'");
        }
        s.updateTimestamp();
    }

    public void archive(long id) {
        Sample s = getById(id);
        if (s.getStatus() == SampleStatus.ARCHIVED) {
            throw new IllegalArgumentException("образец уже ARCHIVED");
        }
        s.setStatus(SampleStatus.ARCHIVED);
        s.updateTimestamp();
    }

    public TreeMap<Long, Sample> getAll() { return samples; }
}