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
        return "register — зарегистрировать нового пользователя";
    }

    @Override
    public boolean execute(String[] args, CliContext context) {
        if (args.length != 1) {
            throw new IllegalArgumentException("используйте: register");
        }

        System.out.print("Логин: ");
        String login = context.getScanner().nextLine().trim();

        System.out.print("Пароль: ");
        String password = context.getScanner().nextLine().trim();

        User user = context.getUserService().register(login, password); //сохранение пользователя в память

        context.getUserStorage().save(context.getUserService().getAllUsers());  //сохранение пользователя в файл

        System.out.println("OK user_id=" + user.getId());
        return true;
    }
}
