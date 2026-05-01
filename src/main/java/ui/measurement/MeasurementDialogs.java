package ui.measurement;

import domain.MeasurementParam;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.MeasurementService;
import ui.common.AlertUtils;
import validation.MeasurementValidator;

import java.util.Optional;

public class MeasurementDialogs {

    private MeasurementDialogs() {
    }

    public static void showAddMeasurementDialog(
            MeasurementService measurementService,
            Runnable afterSuccess
    ) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Measurement");
        dialog.setHeaderText("Create new measurement");

        TextField sampleIdField = new TextField();
        sampleIdField.setPromptText("Sample ID");

        TextField paramField = new TextField();
        paramField.setPromptText("PH / CONDUCTIVITY / TURBIDITY / NITRATE");

        TextField valueField = new TextField();
        valueField.setPromptText("Value");

        TextField unitField = new TextField();
        unitField.setPromptText("Unit");

        TextField methodField = new TextField();
        methodField.setPromptText("Method");

        GridPane form = createMeasurementForm(
                sampleIdField,
                paramField,
                valueField,
                unitField,
                methodField
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                long sampleId = parseSampleId(sampleIdField.getText());

                MeasurementParam param = MeasurementValidator.validateParam(
                        paramField.getText()
                );

                double value = MeasurementValidator.validateValue(
                        valueField.getText()
                );

                String unit = unitField.getText().trim();
                MeasurementValidator.validateUnit(unit);

                String method = methodField.getText().trim();
                MeasurementValidator.validateMethod(method);

                measurementService.add(sampleId, param, value, unit, method);
                afterSuccess.run();
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    private static GridPane createMeasurementForm(
            TextField sampleIdField,
            TextField paramField,
            TextField valueField,
            TextField unitField,
            TextField methodField
    ) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Sample ID:"), 0, 0);
        form.add(sampleIdField, 1, 0);

        form.add(new Label("Param:"), 0, 1);
        form.add(paramField, 1, 1);

        form.add(new Label("Value:"), 0, 2);
        form.add(valueField, 1, 2);

        form.add(new Label("Unit:"), 0, 3);
        form.add(unitField, 1, 3);

        form.add(new Label("Method:"), 0, 4);
        form.add(methodField, 1, 4);

        return form;
    }

    private static long parseSampleId(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("sampleId не может быть пустым");
        }

        try {
            long sampleId = Long.parseLong(raw.trim());

            if (sampleId <= 0) {
                throw new IllegalArgumentException("sampleId должен быть положительным");
            }

            return sampleId;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("sampleId должен быть целым числом");
        }
    }
}