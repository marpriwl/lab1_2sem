package service;

import domain.Measurement;
import domain.MeasurementParam;
import domain.Sample;
import domain.SampleStatus;
import validation.MeasurementValidator;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MeasurementService {
    private final TreeMap<Long, Measurement> measurements = new TreeMap<>();
    private final SampleService sampleService;
    private long nextId = 1;

    public MeasurementService(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    public long add(long sampleId, MeasurementParam param, double value, String unit, String method) {
        Sample sample = sampleService.getById(sampleId);
        if (sample.getStatus() == SampleStatus.ARCHIVED) {
            throw new IllegalArgumentException("нельзя добавлять измерения к ARCHIVED образцу");
        }
        MeasurementValidator.validate(value, unit, method);

        long id = nextId++;
        Measurement m = new Measurement(id, sampleId, param, value, unit, method, "SYSTEM");
        measurements.put(id, m);
        return id;
    }

    public String list(long sampleId, MeasurementParam paramFilter, int lastN) {
        sampleService.getById(sampleId); // check exists
        var list = measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId);
        if (paramFilter != null) {
            list = list.filter(m -> m.getParam() == paramFilter);
        }
        list = list.sorted(Comparator.comparing(Measurement::getMeasuredAt).reversed());
        if (lastN > 0) list = list.limit(lastN);

        return list.map(m -> String.format("%-4d %-12s %-8.3f %-8s %-15s %s",
                        m.getId(), m.getParam(), m.getValue(), m.getUnit(), m.getMethod(),
                        m.getMeasuredAt().toString().substring(0, 19)))
                .collect(Collectors.joining("\n"));
    }

    public String stats(long sampleId, MeasurementParam param) {
        var list = measurements.values().stream()
                .filter(m -> m.getSampleId() == sampleId && m.getParam() == param)
                .toList();
        if (list.isEmpty()) throw new IllegalArgumentException("нет измерений " + param + " для sample=" + sampleId);

        double min = list.stream().mapToDouble(Measurement::getValue).min().orElse(0);
        double max = list.stream().mapToDouble(Measurement::getValue).max().orElse(0);
        double avg = list.stream().mapToDouble(Measurement::getValue).average().orElse(0);

        return String.format("count: %d%nmin: %.3f%nmax: %.3f%navg: %.3f", list.size(), min, max, avg);
    }

    public TreeMap<Long, Measurement> getAll() { return measurements; }
}