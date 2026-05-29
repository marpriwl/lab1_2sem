package cli.command;

import cli.CliContext;
import domain.User;

public class RegisterCommand implements CliCommand {

    @Override
    public String name() {
        return "register";
    }

    @Override
    public String description() {
        return "register - create a new user in PostgreSQL";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Use: register");
        }

        System.out.print("Login: ");
        String login = context.getScanner().nextLine().trim();

        System.out.print("Password: ");
        String password = context.getScanner().nextLine().trim();

        User user = context.getUserService().register(login, password);

        System.out.println("OK user_id=" + user.getId());
        return true;
    }
}
