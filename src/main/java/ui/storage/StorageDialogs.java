package ui.storage;

import domain.Measurement;
import domain.Protocol;
import domain.Sample;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import storage.FileStorage;
import storage.FileValidator;
import storage.LabData;
import ui.common.AlertUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StorageDialogs {

    private StorageDialogs() {
    }

    public static void showSaveDialog(
            Stage stage,
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService
    ) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        try {
            LabData data = new LabData(
                    new ArrayList<>(sampleService.getAll().values()),
                    new ArrayList<>(measurementService.getAll().values()),
                    new ArrayList<>(protocolService.getAll().values())
            );

            FileStorage fileStorage = new FileStorage();
            fileStorage.save(file.toPath(), data);
        } catch (Exception e) {
            AlertUtils.showError(e.getMessage());
        }
    }

    public static void showLoadDialog(
            Stage stage,
            SampleService sampleService,
            MeasurementService measurementService,
            ProtocolService protocolService,
            Runnable afterSuccess
    ) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load JSON");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        try {
            FileStorage fileStorage = new FileStorage();
            FileValidator fileValidator = new FileValidator();

            LabData data = fileStorage.load(file.toPath());
            fileValidator.validate(data);

            TreeMap<Long, Sample> samples = data.getSamples().stream()
                    .collect(Collectors.toMap(
                            Sample::getId,
                            Function.identity(),
                            (a, b) -> a,
                            TreeMap::new
                    ));

            TreeMap<Long, Measurement> measurements = data.getMeasurements().stream()
                    .collect(Collectors.toMap(
                            Measurement::getId,
                            Function.identity(),
                            (a, b) -> a,
                            TreeMap::new
                    ));

            TreeMap<Long, Protocol> protocols = data.getProtocols().stream()
                    .collect(Collectors.toMap(
                            Protocol::getId,
                            Function.identity(),
                            (a, b) -> a,
                            TreeMap::new
                    ));

            sampleService.replaceAll(samples);
            measurementService.replaceAll(measurements);
            protocolService.replaceAll(protocols);

            afterSuccess.run();
        } catch (Exception e) {
            AlertUtils.showError(e.getMessage());
        }
    }
}