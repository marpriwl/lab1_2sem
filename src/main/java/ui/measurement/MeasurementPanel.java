package ui.measurement;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import service.MeasurementService;

public class MeasurementPanel extends VBox {

    private final MeasurementService measurementService;

    public MeasurementPanel(MeasurementService measurementService) {
        this.measurementService = measurementService;

        setSpacing(10);
        setPadding(new Insets(10));

        Label title = new Label("Measurements");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label placeholder = new Label("Measurement cards will be here");

        getChildren().addAll(title, placeholder);
    }
}