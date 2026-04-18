package cli.command;

import cli.CliContext;

public class SampleArchiveCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_archive";
    }

    @Override
    public String description() {
        return "sample_archive <id> — добавить образец в архив";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен id");
        }

        long id = Long.parseLong(args[1]);
        context.getSampleService().archive(id);
        System.out.println("OK sample " + id + " archived");
        return true;
    }
}