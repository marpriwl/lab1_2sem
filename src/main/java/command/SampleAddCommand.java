package cli.command;

import cli.CliContext;

public class SampleAddCommand implements CliCommand {
    @Override
    public String name() {
        return "sample_add";
    }

    @Override
    public String description() {
        return "sample_add — создать образец";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.print("Название: ");
        String name = context.getScanner().nextLine().trim();

        System.out.print("Тип: ");
        String type = context.getScanner().nextLine().trim();

        System.out.print("Место: ");
        String location = context.getScanner().nextLine().trim();

        long id = context.getSampleService().add(name, type, location);
        System.out.println("OK sample_id=" + id);
        return true;
    }
}
