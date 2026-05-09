package storage;

import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AutoStorage {

    private static final Path DATA_PATH = Path.of("data.json");

    private AutoStorage() {
    }

    public static void save(
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService
    ) {
        LabData data = new LabData(
                new ArrayList<>(sampleService.getAll().values()),
                new ArrayList<>(measurementService.getAll().values()),
                new ArrayList<>(protocolService.getAll().values())
        );

        FileStorage fileStorage = new FileStorage();
        fileStorage.save(DATA_PATH, data);
    }

    public static void load(
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService
    ) {
        FileStorage fileStorage = new FileStorage();
        FileValidator fileValidator = new FileValidator();

        LabData data = fileStorage.load(DATA_PATH);
        fileValidator.validate(data);

        TreeMap<Long, Sample> samples = data.getSamples().stream()
                .collect(Collectors.toMap(
                        Sample::getId,
                        Function.identity(),
                        (a, b) -> a,
                        TreeMap::new
                ));

        TreeMap<Long, Measurement> measurements = data.getMeasurements().stream()
                .collect(Collectors.toMap(
                        Measurement::getId,
                        Function.identity(),
                        (a, b) -> a,
                        TreeMap::new
                ));

        TreeMap<Long, Protocol> protocols = data.getProtocols().stream()
                .collect(Collectors.toMap(
                        Protocol::getId,
                        Function.identity(),
                        (a, b) -> a,
                        TreeMap::new
                ));

        sampleService.replaceAll(samples);
        measurementService.replaceAll(measurements);
        protocolService.replaceAll(protocols);
    }
}