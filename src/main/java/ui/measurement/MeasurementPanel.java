package ui.measurement;

import domain.Measurement;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.MeasurementService;
import service.UserService;

public class MeasurementPanel extends VBox {

    private final MeasurementService measurementService;
    private final UserService userService;
    private final MeasurementCardFactory measurementCardFactory;

    private final VBox cardsBox = new VBox(10);

    public MeasurementPanel(MeasurementService measurementService, UserService userService) {
        this.measurementService = measurementService;
        this.userService = userService;
        this.measurementCardFactory = new MeasurementCardFactory();

        setSpacing(10);
        setPadding(new Insets(0));

        Button addMeasurementButton = new Button("Add Measurement");
        addMeasurementButton.setOnAction(event ->
                MeasurementDialogs.showAddMeasurementDialog(
                        measurementService,
                        userService,
                        this::refreshMeasurements
                )
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            measurementService.refreshFromDatabase();
            refreshMeasurements();
        });

        Button statsButton = new Button("Show Stats");
        statsButton.setOnAction(event ->
                MeasurementDialogs.showStatsDialog(measurementService)
        );

        HBox actionsPanel = new HBox(10);
        actionsPanel.getChildren().addAll(addMeasurementButton, statsButton, refreshButton);
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        cardsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardsBox);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(actionsPanel, scrollPane);

        refreshMeasurements();
    }

    private void refreshMeasurements() {
        cardsBox.getChildren().clear();

        if (measurementService.getAll().isEmpty()) {
            Label emptyLabel = new Label("No measurements yet");
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Measurement measurement : measurementService.getAll().values()) {
            VBox card = measurementCardFactory.createMeasurementCard(measurement);
            cardsBox.getChildren().add(card);
        }
    }

    public void refresh() {
        refreshMeasurements();
    }
}
