package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SampleService;

public class SamplePanel extends VBox {

    private final SampleService sampleService;
    private final SampleCardFactory sampleCardFactory;

    private final VBox cardsBox = new VBox(10);

    public SamplePanel(SampleService sampleService) {
        this.sampleService = sampleService;
        this.sampleCardFactory = new SampleCardFactory(sampleService, this::refreshSamples);

        setSpacing(10);
        setPadding(new Insets(0));

        Button addSampleButton = new Button("Add Sample");
        addSampleButton.setOnAction(event ->
                SampleDialogs.showAddSampleDialog(sampleService, this::refreshSamples)
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshSamples());

        HBox actionsPanel = new HBox(10);
        actionsPanel.getChildren().addAll(addSampleButton, refreshButton);
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        cardsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardsBox);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(actionsPanel, scrollPane);

        refreshSamples();
    }

    private void refreshSamples() {
        cardsBox.getChildren().clear();

        if (sampleService.getAll().isEmpty()) {
            Label emptyLabel = new Label("No samples yet");
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Sample sample : sampleService.getAll().values()) {
            VBox card = sampleCardFactory.createSampleCard(sample);
            cardsBox.getChildren().add(card);
        }
    }
}
