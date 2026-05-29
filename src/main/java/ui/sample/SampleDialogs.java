package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.SampleService;
import service.UserService;
import ui.common.AlertUtils;
import validation.SampleValidator;

import java.util.Optional;

public class SampleDialogs {

    private SampleDialogs() {
    }

    public static void showAddSampleDialog(
            SampleService sampleService,
            UserService userService,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Sample");
        dialog.setHeaderText("Create new sample");

        TextField nameField = new TextField();
        TextField typeField = new TextField();
        TextField locationField = new TextField();

        nameField.setPromptText("Name");
        typeField.setPromptText("Type");
        locationField.setPromptText("Location");

        dialog.getDialogPane().setContent(createSampleForm(nameField, typeField, locationField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                long ownerId = userService.requireLogin().getId();

                sampleService.add(
                        nameField.getText().trim(),
                        typeField.getText().trim(),
                        locationField.getText().trim(),
                        ownerId
                );

                afterSuccess.run();
            } catch (Exception exception) {
                AlertUtils.showError(exception.getMessage());
            }
        }
    }

    public static void showEditSampleDialog(
            SampleService sampleService,
            UserService userService,
            Sample sample,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Sample");
        dialog.setHeaderText("Edit sample #" + sample.getId());

        TextField nameField = new TextField(sample.getName());
        TextField typeField = new TextField(sample.getType());
        TextField locationField = new TextField(sample.getLocation());

        dialog.getDialogPane().setContent(createSampleForm(nameField, typeField, locationField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String name = nameField.getText().trim();
                String type = typeField.getText().trim();
                String location = locationField.getText().trim();
                SampleValidator.validate(name, type, location);

                long actorId = userService.requireLogin().getId();
                sampleService.update(sample.getId(), "name", name, actorId);
                sampleService.update(sample.getId(), "type", type, actorId);
                sampleService.update(sample.getId(), "location", location, actorId);

                afterSuccess.run();
            } catch (Exception exception) {
                AlertUtils.showError(exception.getMessage());
            }
        }
    }

    public static void showArchiveSampleDialog(
            SampleService sampleService,
            UserService userService,
            Sample sample,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Archive Sample");
        dialog.setHeaderText("Archive sample #" + sample.getId());

        Label message = new Label(
                "Are you sure you want to archive this sample?\n\n" +
                        "name: " + sample.getName() + "\n" +
                        "type: " + sample.getType() + "\n" +
                        "location: " + sample.getLocation()
        );

        ButtonType archiveButton = new ButtonType("Archive", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().setContent(message);
        dialog.getDialogPane().getButtonTypes().addAll(archiveButton, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == archiveButton) {
            try {
                long actorId = userService.requireLogin().getId();
                sampleService.archive(sample.getId(), actorId);
                afterSuccess.run();
            } catch (Exception exception) {
                AlertUtils.showError(exception.getMessage());
            }
        }
    }

    private static GridPane createSampleForm(
            TextField nameField,
            TextField typeField,
            TextField locationField
    ) {
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

        return form;
    }
}
