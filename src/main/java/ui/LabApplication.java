package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import service.ServiceContext;
import service.UserService;
import ui.auth.AuthDialogs;
import ui.measurement.MeasurementPanel;
import ui.protocol.ProtocolPanel;
import ui.sample.SamplePanel;

import java.util.Optional;

public class LabApplication extends Application {

    private final SampleService sampleService = ServiceContext.getSampleService();
    private final MeasurementService measurementService = ServiceContext.getMeasurementService();
    private final ProtocolService protocolService = ServiceContext.getProtocolService();
    private final UserService userService = ServiceContext.getUserService();

    @Override
    public void start(Stage stage) {
        if (!showStartupAuthDialog()) {
            Platform.exit();
            return;
        }

        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/icons/app-icon.png"))
        );

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #ffe4ec;");

        Label title = new Label("Lab Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button samplesButton = new Button("Samples");
        Button measurementsButton = new Button("Measurements");
        Button protocolsButton = new Button("Protocols");
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
        Button logoutButton = new Button("Logout");

        Label authLabel = new Label();

        Runnable refreshAuthLabel = () -> {
            if (userService.isLoggedIn()) {
                authLabel.setText(
                        "User: " +
                                userService.getCurrentUser().getLogin() +
                                " (id=" + userService.getCurrentUser().getId() + ")"
                );
            } else {
                authLabel.setText("User: not logged in");
            }
        };

        refreshAuthLabel.run();

        HBox topPanel = new HBox(10);
        topPanel.getChildren().addAll(
                title,
                authLabel,
                registerButton,
                loginButton,
                logoutButton,
                samplesButton,
                measurementsButton,
                protocolsButton
        );
        topPanel.setPadding(new Insets(0, 0, 12, 0));

        SamplePanel samplePanel = new SamplePanel(sampleService, userService);
        MeasurementPanel measurementPanel = new MeasurementPanel(measurementService, userService);
        ProtocolPanel protocolPanel = new ProtocolPanel(protocolService, measurementService, userService);

        registerButton.setOnAction(event ->
                AuthDialogs.showRegisterDialog(
                        userService,
                        refreshAuthLabel
                )
        );

        loginButton.setOnAction(event ->
                AuthDialogs.showLoginDialog(
                        userService,
                        refreshAuthLabel
                )
        );

        logoutButton.setOnAction(event ->
                AuthDialogs.logout(
                        userService,
                        refreshAuthLabel
                )
        );

        samplesButton.setOnAction(event -> root.setCenter(samplePanel));
        measurementsButton.setOnAction(event -> root.setCenter(measurementPanel));
        protocolsButton.setOnAction(event -> root.setCenter(protocolPanel));

        root.setTop(topPanel);
        root.setCenter(samplePanel);

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Lab Manager");
        stage.setScene(scene);
        stage.show();
    }

    private boolean showStartupAuthDialog() {
        while (!userService.isLoggedIn()) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Authorization");
            dialog.setHeaderText("Login is required before opening the main window");

            Label message = new Label("Choose Login if you already have an account, or Register to create one.");
            dialog.getDialogPane().setContent(message);

            ButtonType loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            ButtonType registerButton = new ButtonType("Register", ButtonBar.ButtonData.APPLY);
            ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getDialogPane().getButtonTypes().addAll(loginButton, registerButton, exitButton);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isEmpty() || result.get() == exitButton) {
                return false;
            }

            if (result.get() == loginButton) {
                AuthDialogs.showLoginDialog(userService, () -> { });
            } else if (result.get() == registerButton) {
                AuthDialogs.showRegisterDialog(userService, () -> { });
            }
        }

        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
