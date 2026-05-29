package ui.auth;

import domain.User;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.UserService;
import ui.common.AlertUtils;

import java.util.Optional;

public class AuthDialogs {

    private AuthDialogs() {
    }

    public static void showRegisterDialog(
            UserService userService,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register");
        dialog.setHeaderText("Create new user");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        GridPane form = createAuthForm(loginField, passwordField);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String login = loginField.getText().trim();
                String password = passwordField.getText().trim();

                User user = userService.register(login, password);

                System.out.println("OK user_id=" + user.getId());
                afterSuccess.run();
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    public static void showLoginDialog(
            UserService userService,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Login to account");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        GridPane form = createAuthForm(loginField, passwordField);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String login = loginField.getText().trim();
                String password = passwordField.getText().trim();

                userService.login(login, password);

                afterSuccess.run();
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    public static void logout(
            UserService userService,
            Runnable afterSuccess
    ) {
        try {
            if (!userService.isLoggedIn()) {
                throw new IllegalStateException("вы не вошли в систему");
            }

            userService.logout();
            afterSuccess.run();
        } catch (Exception e) {
            AlertUtils.showError(e.getMessage());
        }
    }

    private static GridPane createAuthForm(
            TextField loginField,
            PasswordField passwordField
    ) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Login:"), 0, 0);
        form.add(loginField, 1, 0);

        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);

        return form;
    }
}
