package cli.command;

import cli.CliContext;
import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import storage.FileStorage;
import storage.LabData;

import java.nio.file.Path;
import java.util.ArrayList;

public class SaveCommand implements CliCommand {
    private final FileStorage fileStorage = new FileStorage();

    @Override
    public String name() {
        return "save";
    }

    @Override
    public String description() {
        return "save <path> — сохранить данные в JSON-файл";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен путь к файлу");
        }

        Path path = Path.of(args[1]);

        LabData data = new LabData(
                new ArrayList<Sample>(context.getSampleService().getAll().values()),
                new ArrayList<Measurement>(context.getMeasurementService().getAll().values()),
                new ArrayList<Protocol>(context.getProtocolService().getAll().values())
        );

        fileStorage.save(path, data);

        System.out.println("OK saved to " + path);
        return true;
    }
}