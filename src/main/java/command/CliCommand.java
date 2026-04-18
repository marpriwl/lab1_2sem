package cli.command;

import cli.CliContext;

public interface CliCommand {
    String name();
    String description();
    boolean execute(String[] args, CliContext context);
}