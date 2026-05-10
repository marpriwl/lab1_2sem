package cli.command;

import cli.CliContext;

public class LogoutCommand implements CliCommand {

    @Override
    public String name() {
        return "logout";
    }

    @Override
    public String description() {
        return "logout — выйти из текущего пользователя";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length != 1) {
            throw new IllegalArgumentException("используйте: logout");
        }

        if (!context.getUserService().isLoggedIn()) {
            throw new IllegalStateException("вы не вошли в систему");
        }

        context.getUserService().logout();

        System.out.println("OK logged out");
        return true;
    }
}

