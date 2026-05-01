package ui;

import domain.Sample;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LabApplication extends Application {

    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();

    private VBox cardsBox;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("Lab Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button samplesButton = new Button("Samples");
        Button measurementsButton = new Button("Measurements");
        Button protocolsButton = new Button("Protocols");

        Button saveButton = new Button("Save JSON");
        Button loadButton = new Button("Load JSON");

        HBox topPanel = new HBox(10);
        topPanel.getChildren().addAll(
                title,
                samplesButton,
                measurementsButton,
                protocolsButton,
                saveButton,
                loadButton
        );
        topPanel.setPadding(new Insets(0, 0, 12, 0));

        Button addSampleButton = new Button("Add Sample");
        addSampleButton.setOnAction(event -> showAddSampleDialog());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshSamples());

        HBox actionsPanel = new HBox(10);
        actionsPanel.getChildren().addAll(addSampleButton, refreshButton);
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        VBox topContainer = new VBox(8);
        topContainer.getChildren().addAll(topPanel, actionsPanel);

        cardsBox = new VBox(10);
        cardsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardsBox);
        scrollPane.setFitToWidth(true);

        root.setTop(topContainer);
        root.setCenter(scrollPane);

        refreshSamples();

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Lab Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void showAddSampleDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Sample");
        dialog.setHeaderText("Create new sample");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField typeField = new TextField();
        typeField.setPromptText("Type");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);

        form.add(new Label("Type:"), 0, 1);
        form.add(typeField, 1, 1);

        form.add(new Label("Location:"), 0, 2);
        form.add(locationField, 1, 2);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String type = typeField.getText().trim();
                    String location = locationField.getText().trim();

                    sampleService.add(name, type, location);
                    refreshSamples();
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshSamples() {
        cardsBox.getChildren().clear();

        if (sampleService.getAll().isEmpty()) {
            Label emptyLabel = new Label("No samples yet");
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Sample sample : sampleService.getAll().values()) {
            VBox card = createSampleCard(sample);
            cardsBox.getChildren().add(card);
        }
    }

    private VBox createSampleCard(Sample sample) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-background-color: #f8f8f8;"
        );

        Label title = new Label("Sample #" + sample.getId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label name = new Label("name: " + sample.getName());
        Label type = new Label("type: " + sample.getType());
        Label location = new Label("location: " + sample.getLocation());
        Label status = new Label("status: " + sample.getStatus());
        Label owner = new Label("owner: " + sample.getOwnerUsername());

        HBox buttons = new HBox(8);
        Button editButton = new Button("Edit");
        Button archiveButton = new Button("Archive");

        buttons.getChildren().addAll(editButton, archiveButton);

        card.getChildren().addAll(
                title,
                name,
                type,
                location,
                status,
                owner,
                buttons
        );

        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}