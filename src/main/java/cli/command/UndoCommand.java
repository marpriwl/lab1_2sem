package cli.command;

import cli.CliContext;

public class UndoCommand implements CliCommand {
    @Override
    public String name() {
        return "undo";
    }

    @Override
    public String description() {
        return "undo — отменить последнее действие";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.println(context.getHistoryService().undo());
        return true;
    }
}
