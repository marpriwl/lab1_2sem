package cli.command;

import cli.CliContext;

public class SampleListCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_list";
    }

    @Override
    public String description() {
        return "sample_list [--status ACTIVE|ARCHIVED] — вывести список образцов";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        String status = null;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--status") && i + 1 < args.length) {
                status = args[i + 1];
            }
        }

        System.out.println("ID   Name                 Type       Location        Status");
        System.out.println(context.getSampleService().list(status));
        return true;
    }
}
