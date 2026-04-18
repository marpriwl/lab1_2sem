package cli.command;

import cli.CliContext;

public class SampleShowCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_show";
    }

    @Override
    public String description() {
        return "sample_show <id> — показать карточку образца";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 2) {
            throw new IllegalArgumentException("нужен id");
        }

        long id = Long.parseLong(args[1]);
        var sample = context.getSampleService().getById(id);

        System.out.printf(
                "Sample #%d%nname: %s%ntype: %s%nlocation: %s%nstatus: %s%nowner: %s%nmeasurements: %d%n",
                sample.getId(),
                sample.getName(),
                sample.getType(),
                sample.getLocation(),
                sample.getStatus(),
                sample.getOwnerUsername(),
                0
        );

        return true;
    }
}
