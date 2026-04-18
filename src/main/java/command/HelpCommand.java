package cli.command;

import cli.CliContext;

import java.util.Collection;
import java.util.function.Supplier;

public class HelpCommand implements CliCommand {
    private final Supplier<Collection<CliCommand>> commandsSupplier;

    public HelpCommand(Supplier<Collection<CliCommand>> commandsSupplier) {
        this.commandsSupplier = commandsSupplier;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "help — показать список команд";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.println("Команды:");
        for (CliCommand command : commandsSupplier.get()) {
            System.out.println(command.description());
        }
        return true;
    }
}
