package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SampleService;

public class SampleCardFactory {

    private final SampleService sampleService;
    private final Runnable afterChange;

    public SampleCardFactory(SampleService sampleService, Runnable afterChange) {
        this.sampleService = sampleService;
        this.afterChange = afterChange;
    }

    public VBox createSampleCard(Sample sample) {
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
        editButton.setOnAction(event ->
                SampleDialogs.showEditSampleDialog(sampleService, sample, afterChange)
        );

        Button archiveButton = new Button("Archive");
        archiveButton.setOnAction(event ->
                SampleDialogs.showArchiveSampleDialog(sampleService, sample, afterChange)
        );

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
}
