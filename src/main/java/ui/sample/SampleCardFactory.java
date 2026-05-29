package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SampleService;
import service.UserService;

public class SampleCardFactory {

    private final SampleService sampleService;
    private final UserService userService;
    private final Runnable afterChange;

    public SampleCardFactory(
            SampleService sampleService,
            UserService userService,
            Runnable afterChange
    ) {
        this.sampleService = sampleService;
        this.userService = userService;
        this.afterChange = afterChange;
    }

    public VBox createSampleCard(Sample sample) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-border-color: #F2BED1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-background-color: #F9F5F6;"
        );

        Label title = new Label("Sample #" + sample.getId());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label name = new Label("name: " + sample.getName());
        Label type = new Label("type: " + sample.getType());
        Label location = new Label("location: " + sample.getLocation());
        Label status = new Label("status: " + sample.getStatus());
        Label owner = new Label("Owner: " + sample.getOwnerId());

        Button editButton = new Button("Edit");
        editButton.setOnAction(event ->
                SampleDialogs.showEditSampleDialog(sampleService, userService, sample, afterChange)
        );

        Button archiveButton = new Button("Archive");
        archiveButton.setOnAction(event ->
                SampleDialogs.showArchiveSampleDialog(sampleService, userService, sample, afterChange)
        );

        boolean canEdit = userService.isLoggedIn()
                && userService.getCurrentUser().getId() == sample.getOwnerId();
        editButton.setDisable(!canEdit);
        archiveButton.setDisable(!canEdit);

        HBox buttons = new HBox(8);
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
