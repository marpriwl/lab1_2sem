package cli.command;

import cli.CliContext;
import service.history.operations.ArchiveSampleOperation;

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
        long actorId = context.getUserService().requireLogin().getId();

        if (args.length < 2) {
            throw new IllegalArgumentException("нужен id");
        }

        long id = Long.parseLong(args[1]);
        context.getSampleService().archive(id, actorId);
        context.getHistoryService().addOperation(new ArchiveSampleOperation(context.getSampleService(), id, actorId));
        
        System.out.println("OK sample " + id + " archived");
        return true;
    }
}
