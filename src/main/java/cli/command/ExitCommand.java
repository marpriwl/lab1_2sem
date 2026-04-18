package cli.command;

import cli.CliContext;

public class ExitCommand implements CliCommand {
    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String description() {
        return "exit — завершить программу";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.println("До свидания!");
        return false;
    }
}
