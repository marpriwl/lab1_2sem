package cli.command;

import cli.CliContext;
import domain.User;

public class LoginCommand implements CliCommand {

    @Override
    public String name() {
        return "login";
    }

    @Override
    public String description() {
        return "login — войти в систему";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length != 1) {
            throw new IllegalArgumentException("используйте: login");
        }

        System.out.print("Логин: ");
        String login = context.getScanner().nextLine().trim();

        System.out.print("Пароль: ");
        String password = context.getScanner().nextLine().trim();

        User user = context.getUserService().login(login, password);

        System.out.println("OK logged in as " + user.getLogin());

        return true;
    }
}
