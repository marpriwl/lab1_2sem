package ui.protocol;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import service.ProtocolService;

public class ProtocolPanel extends VBox {

    private final ProtocolService protocolService;

    public ProtocolPanel(ProtocolService protocolService) {
        this.protocolService = protocolService;

        setSpacing(10);
        setPadding(new Insets(10));

        Label title = new Label("Protocols");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label placeholder = new Label("Protocol cards will be here");

        getChildren().addAll(title, placeholder);
    }
}
