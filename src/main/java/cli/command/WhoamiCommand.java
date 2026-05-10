package cli.command;

import cli.CliContext;
import domain.User;

public class WhoamiCommand implements CliCommand {

    @Override
    public String name() {
        return "whoami";
    }

    @Override
    public String description() {
        return "whoami — показать текущего пользователя";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length != 1) {
            throw new IllegalArgumentException("используйте: whoami");
        }
        if (!context.getUserService().isLoggedIn()) {
            System.out.println("Вы не вошли в систему");
            return true;
        }

        User user = context.getUserService().getCurrentUser();

        System.out.println("current user: " + user.getLogin() + " (id=" + user.getId() + ")");
        return true;
    }
}
