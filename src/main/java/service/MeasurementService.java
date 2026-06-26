package service;

import domain.Measurement;
import domain.MeasurementParam;
import domain.Sample;
import domain.SampleStatus;
import storage.DbStorage;
import validation.MeasurementValidator;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MeasurementService {
    private final TreeMap<Long, Measurement> measurements = new TreeMap<>();
    private final SampleService sampleService;
    private final DbStorage dbStorage;

    public MeasurementService(SampleService sampleService) {
        this(sampleService, null);
    }

    public MeasurementService(SampleService sampleService, DbStorage dbStorage) {
        this.sampleService = sampleService;
        this.dbStorage = dbStorage;
    }

    public long add(
            long sampleId,
            MeasurementParam param,
            double value,
            String unit,
            String method,
            long ownerId
    ) {
        Sample sample = sampleService.getById(sampleId);

        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new IllegalArgumentException("Cannot add measurements to archived sample");
        }

        MeasurementValidator.validate(value, unit, method);

        long id = dbStorage == null ? IdGenerator.nextMeasurementId() : 0;
        Measurement measurement = new Measurement(id, sampleId, param, value, unit, method, ownerId);

        if (dbStorage != null) {
            measurement = dbStorage.insertMeasurement(measurement);
            id = measurement.getId();
        }

        measurements.put(id, measurement);
        return id;
    }

    public Measurement getById(long id) {
        Measurement measurement = measurements.get(id);
        if (measurement == null) {
            throw new IllegalArgumentException("Measurement id=" + id + " was not found");
        }
        return measurement;
    }

    public String list(long sampleId, MeasurementParam paramFilter, int lastN) {
        sampleService.getById(sampleId);

        var list = measurements.values().stream()
                .filter(measurement -> measurement.getSampleId() == sampleId);

        if (paramFilter != null) {
            list = list.filter(measurement -> measurement.getParam() == paramFilter);
        }

        list = list.sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed());

        if (lastN > 0) {
            list = list.limit(lastN);
        }

        return list.map(measurement -> String.format(
                        "%-4d %-12s %-8.3f %-8s %-15s %s owner=%d",
                        measurement.getId(),
                        measurement.getParam(),
                        measurement.getValue(),
                        measurement.getUnit(),
                        measurement.getMethod(),
                        measurement.getMeasuredAt().toString().substring(0, 19),
                        measurement.getOwnerId()
                ))
                .collect(Collectors.joining("\n"));
    }

    public String stats(long sampleId, MeasurementParam param) {
        var list = measurements.values().stream()
                .filter(measurement -> measurement.getSampleId() == sampleId && measurement.getParam() == param)
                .toList();

        if (list.isEmpty()) {
            throw new IllegalArgumentException("No measurements " + param + " for sample=" + sampleId);
        }

        double min = list.stream().mapToDouble(Measurement::getValue).min().orElse(0);
        double max = list.stream().mapToDouble(Measurement::getValue).max().orElse(0);
        double avg = list.stream().mapToDouble(Measurement::getValue).average().orElse(0);

        return String.format("count: %d%nmin: %.3f%nmax: %.3f%navg: %.3f", list.size(), min, max, avg);
    }

    public void delete(long id) {
        measurements.remove(id);
        if (dbStorage != null) {
            dbStorage.deleteMeasurement(id);
        }
    }

    public void restore(Measurement measurement) {
        measurements.put(measurement.getId(), measurement);
        if (dbStorage != null) {
            dbStorage.insertMeasurement(measurement);
        }
    }

    public void replaceAll(TreeMap<Long, Measurement> loadedMeasurements) {
        measurements.clear();
        measurements.putAll(loadedMeasurements);

        long nextId = measurements.isEmpty()
                ? 1
                : measurements.lastKey() + 1;

        IdGenerator.setMeasurementId(nextId);
    }

    public void refreshFromDatabase() {
        if (dbStorage == null) {
            return;
        }

        TreeMap<Long, Measurement> loadedMeasurements = dbStorage.loadLabData()
                .getMeasurements()
                .stream()
                .collect(Collectors.toMap(
                        Measurement::getId,
                        measurement -> measurement,
                        (left, right) -> left,
                        TreeMap::new
                ));

        replaceAll(loadedMeasurements);
    }

    public TreeMap<Long, Measurement> getAll() {
        return measurements;
    }
}
