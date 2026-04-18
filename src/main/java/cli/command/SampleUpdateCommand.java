package cli.command;

import cli.CliContext;

public class SampleUpdateCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_update";
    }

    @Override
    public String description() {
        return "sample_update <id> field=value — поменять параметр образца";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length < 3) {
            throw new IllegalArgumentException("нужен id и field=value");
        }

        long id = Long.parseLong(args[1]);

        for (int i = 2; i < args.length; i++) {
            String[] kv = args[i].split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            context.getSampleService().update(id, kv[0], kv[1]);
        }

        System.out.println("OK");
        return true;
    }
}