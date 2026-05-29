package cli;

import cli.command.CliCommand;
import cli.command.ExitCommand;
import cli.command.HelpCommand;
import cli.command.LoginCommand;
import cli.command.LogoutCommand;
import cli.command.MeasAddCommand;
import cli.command.MeasListCommand;
import cli.command.MeasStatsCommand;
import cli.command.ProtApplyCommand;
import cli.command.ProtCreateCommand;
import cli.command.RegisterCommand;
import cli.command.SampleAddCommand;
import cli.command.SampleArchiveCommand;
import cli.command.SampleListCommand;
import cli.command.SampleShowCommand;
import cli.command.SampleUpdateCommand;
import cli.command.WhoamiCommand;
import db.DataBaseInitializer;
import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import domain.User;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import service.UserService;
import storage.DbStorage;
import storage.LabData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LabCli {
    private final DbStorage dbStorage;
    private final SampleService sampleService;
    private final MeasurementService measurementService;
    private final ProtocolService protocolService;
    private final Scanner scanner;
    private final UserService userService;
    private final CliContext context;
    private final Map<String, CliCommand> commands = new LinkedHashMap<>();

    public LabCli() {
        this(null);
    }

    public LabCli(String[] args) {
        this.dbStorage = createDbStorage();
        this.sampleService = new SampleService(dbStorage);
        this.measurementService = new MeasurementService(sampleService, dbStorage);
        this.protocolService = new ProtocolService(dbStorage);
        this.scanner = new Scanner(System.in);
        this.userService = new UserService(loadUsers(), dbStorage);
        this.context = new CliContext(
                sampleService,
                measurementService,
                protocolService,
                scanner,
                userService
        );

        registerCommands();
        loadDataFromDatabase();

        if (args != null && args.length > 0) {
            System.out.println("File autoload is disabled in DB mode; data is loaded from PostgreSQL.");
        }
    }

    private void registerCommands() {
        register(new HelpCommand(() -> commands.values()));
        register(new ExitCommand());

        register(new RegisterCommand());
        register(new LoginCommand());
        register(new LogoutCommand());
        register(new WhoamiCommand());

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

    private DbStorage createDbStorage() {
        try {
            DataBaseInitializer.init();
            return new DbStorage();
        } catch (Exception exception) {
            System.out.println("DB mode is unavailable: " + exception.getMessage());
            System.out.println("Application will continue in memory mode.");
            return null;
        }
    }

    private List<User> loadUsers() {
        if (dbStorage == null) {
            return new ArrayList<>();
        }

        return dbStorage.findAllUsers();
    }

    private void loadDataFromDatabase() {
        if (dbStorage == null) {
            return;
        }

        LabData data = dbStorage.loadLabData();

        TreeMap<Long, Sample> samples = data.getSamples().stream()
                .collect(Collectors.toMap(
                        Sample::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));

        TreeMap<Long, Measurement> measurements = data.getMeasurements().stream()
                .collect(Collectors.toMap(
                        Measurement::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));

        TreeMap<Long, Protocol> protocols = data.getProtocols().stream()
                .collect(Collectors.toMap(
                        Protocol::getId,
                        Function.identity(),
                        (left, right) -> left,
                        TreeMap::new
                ));

        sampleService.replaceAll(samples);
        measurementService.replaceAll(measurements);
        protocolService.replaceAll(protocols);
    }

    public void start() {
        System.out.println("Type help to show commands.");

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            try {
                running = processCommand(line);
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
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
            throw new IllegalArgumentException("Unknown command");
        }

        return command.execute(parts, context);
    }
}
