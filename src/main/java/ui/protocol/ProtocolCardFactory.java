package ui.protocol;

import domain.Protocol;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class ProtocolCardFactory {

    public VBox createProtocolCard(Protocol protocol) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-border-color: #F2BED1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-background-color: #F9F5F6;"
        );

        Label title = new Label("Protocol #" + protocol.getId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label name = new Label("name: " + protocol.getName());

        String requiredParams = protocol.getRequiredParams().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        Label params = new Label("required params: " + requiredParams);
        Label owner = new Label("owner: " + protocol.getOwnerUsername());
        Label createdAt = new Label("createdAt: " + protocol.getCreatedAt());
        Label updatedAt = new Label("updatedAt: " + protocol.getUpdatedAt());

        card.getChildren().addAll(
                title,
                name,
                params,
                owner,
                createdAt,
                updatedAt
        );

        return card;
    }
}
