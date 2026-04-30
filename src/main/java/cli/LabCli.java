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
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import cli.command.SaveCommand;
import cli.command.LoadCommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class LabCli {
    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();
    private final Scanner scanner = new Scanner(System.in);

    private final CliContext context =
            new CliContext(sampleService, measurementService, protocolService, scanner);

    private final Map<String, CliCommand> commands = new LinkedHashMap<>();

    public LabCli() {
        register(new HelpCommand(() -> commands.values()));
        register(new ExitCommand());
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
        register(new SaveCommand());
        register(new LoadCommand());
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
