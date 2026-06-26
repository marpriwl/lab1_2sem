package cli.command;

import cli.CliContext;

public class RedoCommand implements CliCommand {
    @Override
    public String name() {
        return "redo";
    }

    @Override
    public String description() {
        return "redo — повторить отмененное действие";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        System.out.println(context.getHistoryService().redo());
        return true;
    }
}
