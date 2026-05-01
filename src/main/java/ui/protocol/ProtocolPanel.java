package ui.protocol;

import domain.Protocol;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.MeasurementService;
import service.ProtocolService;

public class ProtocolPanel extends VBox {

    private final ProtocolService protocolService;
    private final MeasurementService measurementService;
    private final ProtocolCardFactory protocolCardFactory;

    private final VBox cardsBox = new VBox(10);

    public ProtocolPanel(
            ProtocolService protocolService,
            MeasurementService measurementService
    ) {
        this.protocolService = protocolService;
        this.measurementService = measurementService;
        this.protocolCardFactory = new ProtocolCardFactory();

        setSpacing(10);
        setPadding(new Insets(0));

        Button addProtocolButton = new Button("Add Protocol");
        addProtocolButton.setOnAction(event ->
                ProtocolDialogs.showAddProtocolDialog(
                        protocolService,
                        this::refreshProtocols
                )
        );

        Button applyProtocolButton = new Button("Apply Protocol");
        applyProtocolButton.setOnAction(event ->
                ProtocolDialogs.showApplyProtocolDialog(
                        protocolService,
                        measurementService
                )
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshProtocols());

        HBox actionsPanel = new HBox(10);
        actionsPanel.getChildren().addAll(
                addProtocolButton,
                applyProtocolButton,
                refreshButton
        );
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        cardsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardsBox);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(actionsPanel, scrollPane);

        refreshProtocols();
    }

    private void refreshProtocols() {
        cardsBox.getChildren().clear();

        if (protocolService.getAll().isEmpty()) {
            Label emptyLabel = new Label("No protocols yet");
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Protocol protocol : protocolService.getAll().values()) {
            VBox card = protocolCardFactory.createProtocolCard(protocol);
            cardsBox.getChildren().add(card);
        }
    }
}
