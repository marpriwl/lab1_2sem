package cli.command;

import cli.CliContext;
import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import storage.FileStorage;
import storage.FileValidator;
import storage.LabData;

import java.nio.file.Path;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadCommand implements CliCommand {
    private final FileStorage fileStorage = new FileStorage();
    private final FileValidator fileValidator = new FileValidator();

    @Override
    public String name() {
        return "load";
    }

    @Override
    public String description() {
        return "load <path> — загрузить данные из JSON-файла";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен путь к файлу");
        }

        Path path = Path.of(args[1]);

        LabData data = fileStorage.load(path);
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

        context.getSampleService().replaceAll(samples);
        context.getMeasurementService().replaceAll(measurements);
        context.getProtocolService().replaceAll(protocols);

        System.out.println("OK loaded from " + path);
        return true;
    }
}