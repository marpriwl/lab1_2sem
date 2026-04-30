package cli;

import cli.command.CliCommand;
import cli.command.ExitCommand;
import cli.command.HelpCommand;
import cli.command.MeasAddCommand;
import cli.command.MeasListCommand;
import cli.command.MeasStatsCommand;
import cli.command.ProtApplyCommand;
import cli.command.ProtCreateCommand;
import cli.command.SampleAddCommand;
import cli.command.SampleArchiveCommand;
import cli.command.SampleListCommand;
import cli.command.SampleShowCommand;
import cli.command.SampleUpdateCommand;
import cli.command.SaveCommand;
import cli.command.LoadCommand;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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

public class LabCli {
    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();
    private final Scanner scanner = new Scanner(System.in);

    private final CliContext context =
            new CliContext(sampleService, measurementService, protocolService, scanner);

    private final Map<String, CliCommand> commands = new LinkedHashMap<>();

    public LabCli() {
        registerCommands();
    }

    public LabCli(String[] args) {
        registerCommands();

        if (args != null && args.length > 0) {
            loadAtStartup(args[0]);
        }
    }

    private void registerCommands() {
        register(new HelpCommand(() -> commands.values()));
        register(new ExitCommand());
        register(new SaveCommand());
        register(new LoadCommand());

        register(new SampleAddCommand());
        register(new SampleListCommand());
        register(new SampleShowCommand());
        register(new SampleUpdateCommand());
        register(new SampleArchiveCommand());
        register(new MeasAddCommand());
        register(new MeasListCommand());
        register(new MeasStatsCommand());
        register(new ProtCreateCommand());
        register(new ProtApplyCommand());
    }

    private void loadAtStartup(String filePath) {
        try {
            FileStorage fileStorage = new FileStorage();
            FileValidator fileValidator = new FileValidator();

            LabData data = fileStorage.load(Path.of(filePath));
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

            System.out.println("OK loaded from " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка автозагрузки: " + e.getMessage());
            System.out.println("Программа запущена без загруженных данных.");
        }
    }

    public void start() {
        System.out.println("Введите help для списка команд.");

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            try {
                running = processCommand(line);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void register(CliCommand command) {
        commands.put(command.name().toLowerCase(), command);
    }

    private boolean processCommand(String line) {
        String[] parts = line.split("\\s+");
        String commandName = parts[0].toLowerCase();

        CliCommand command = commands.get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("неизвестная команда");
        }

        return command.execute(parts, context);
    }
}
