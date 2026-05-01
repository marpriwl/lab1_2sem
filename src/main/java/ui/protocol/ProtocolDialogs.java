package ui.protocol;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.MeasurementService;
import service.ProtocolService;
import ui.common.AlertUtils;
import validation.ProtocolValidator;

import java.util.Optional;

public class ProtocolDialogs {

    private ProtocolDialogs() {
    }

    public static void showAddProtocolDialog(
            ProtocolService protocolService,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Protocol");
        dialog.setHeaderText("Create new protocol");

        TextField nameField = new TextField();
        nameField.setPromptText("Protocol name");

        TextField paramsField = new TextField();
        paramsField.setPromptText("PH, CONDUCTIVITY, TURBIDITY, NITRATE");

        GridPane form = createProtocolForm(nameField, paramsField);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String name = nameField.getText().trim();

                protocolService.create(
                        name,
                        ProtocolValidator.parseParams(paramsField.getText())
                );

                afterSuccess.run();
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    public static void showApplyProtocolDialog(
            ProtocolService protocolService,
            MeasurementService measurementService
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Apply Protocol");
        dialog.setHeaderText("Check protocol completeness for sample");

        TextField protocolIdField = new TextField();
        protocolIdField.setPromptText("Protocol ID");

        TextField sampleIdField = new TextField();
        sampleIdField.setPromptText("Sample ID");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Protocol ID:"), 0, 0);
        form.add(protocolIdField, 1, 0);

        form.add(new Label("Sample ID:"), 0, 1);
        form.add(sampleIdField, 1, 1);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                long protocolId = parsePositiveLong(protocolIdField.getText(), "protocolId");
                long sampleId = parsePositiveLong(sampleIdField.getText(), "sampleId");

                String applyResult = protocolService.apply(
                        protocolId,
                        sampleId,
                        measurementService
                );

                showResultDialog(applyResult);
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    private static GridPane createProtocolForm(
            TextField nameField,
            TextField paramsField
    ) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);

        form.add(new Label("Required params:"), 0, 1);
        form.add(paramsField, 1, 1);

        return form;
    }

    private static long parsePositiveLong(String raw, String fieldName) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }

        try {
            long value = Long.parseLong(raw.trim());

            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " должен быть положительным");
            }

            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должен быть целым числом");
        }
    }

    private static void showResultDialog(String result) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Protocol result");
        dialog.setHeaderText("Protocol check result");

        TextArea resultArea = new TextArea(result);
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefWidth(400);
        resultArea.setPrefHeight(120);

        dialog.getDialogPane().setContent(resultArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }
}