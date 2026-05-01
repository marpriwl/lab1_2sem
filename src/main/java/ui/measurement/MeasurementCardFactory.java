package ui.measurement;

import domain.Measurement;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MeasurementCardFactory {

    public VBox createMeasurementCard(Measurement measurement) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-background-color: #f8f8f8;"
        );

        Label title = new Label("Measurement #" + measurement.getId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label sampleId = new Label("sampleId: " + measurement.getSampleId());
        Label param = new Label("param: " + measurement.getParam());
        Label value = new Label("value: " + measurement.getValue());
        Label unit = new Label("unit: " + measurement.getUnit());
        Label method = new Label("method: " + measurement.getMethod());
        Label measuredAt = new Label("measuredAt: " + measurement.getMeasuredAt());
        Label owner = new Label("owner: " + measurement.getOwnerUsername());

        card.getChildren().addAll(
                title,
                sampleId,
                param,
                value,
                unit,
                method,
                measuredAt,
                owner
        );

        return card;
    }
}
