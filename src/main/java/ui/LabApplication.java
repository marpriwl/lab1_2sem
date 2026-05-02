package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;
import ui.sample.SamplePanel;

import ui.measurement.MeasurementPanel;
import ui.protocol.ProtocolPanel;
import ui.storage.StorageDialogs;

import javafx.scene.image.Image;


public class LabApplication extends Application {   //Application — базовый класс JavaFX-приложения.

    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();

    @Override
    public void start(Stage stage) {  //Если класс наследуется от Application, JavaFX ожидает, что в нём будет метод: start(Stage stage). Это главный метод JavaFX. Он вызывается автоматически при запуске приложения. Stage — это главное окно.

        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/icons/app-icon.png"))
        );

        BorderPane root = new BorderPane(); //BorderPane — контейнер с зонами.
        root.setPadding(new Insets(12)); //Добавляет внутренний отступ 12 px со всех сторон.

        Label title = new Label("Lab Manager"); //Label — текстовая надпись.
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;"); //setStyle(...) задает CSS-стиль.
        root.setStyle("-fx-background-color: #ffe4ec;");

        Button samplesButton = new Button("Samples");
        Button measurementsButton = new Button("Measurements");
        Button protocolsButton = new Button("Protocols");

        Button saveButton = new Button("Save JSON");
        Button loadButton = new Button("Load JSON");

        HBox topPanel = new HBox(10); //HBox — горизонтальный контейнер, размещает эл-ты слева направо, 10 — расстояние между элементами.
        topPanel.getChildren().addAll( //getChildren() — список элементов внутри контейнера.
                title,                 //addAll(...) добавляет в HBox сразу несколько элементов.
                samplesButton,
                measurementsButton,
                protocolsButton,
                saveButton,
                loadButton
        );
        topPanel.setPadding(new Insets(0, 0, 12, 0)); //Добавляет отступ. Формат: new Insets(top, right, bottom, left).

        SamplePanel samplePanel = new SamplePanel(sampleService); //create SamplePanel — это отдельный JavaFX-блок, который отвечает за экран образцов.
        MeasurementPanel measurementPanel = new MeasurementPanel(measurementService);
        ProtocolPanel protocolPanel = new ProtocolPanel(protocolService, measurementService);

        samplesButton.setOnAction(event -> root.setCenter(samplePanel));
        measurementsButton.setOnAction(event -> root.setCenter(measurementPanel));
        protocolsButton.setOnAction(event -> root.setCenter(protocolPanel));


        saveButton.setOnAction(event ->
                StorageDialogs.showSaveDialog(
                        stage,
                        sampleService,
                        measurementService,
                        protocolService
                )
        );

        loadButton.setOnAction(event ->
                StorageDialogs.showLoadDialog(
                        stage,
                        sampleService,
                        measurementService,
                        protocolService,
                        () -> {
                            samplePanel.refresh();
                            measurementPanel.refresh();
                            protocolPanel.refresh();
                        }
                )
        );

        root.setTop(topPanel); //положить topPanel в верхнюю часть окна
        root.setCenter(samplePanel); //Эта строка кладёт экран образцов в центральную часть окна.

        Scene scene = new Scene(root, 900, 600);  //Создание сцены: Scene — содержимое окна; root — главный контейнер; 900, 600 — размеры окна.

        stage.setTitle("Lab Manager");  //Настройка окна. Заголовок окна.
        stage.setScene(scene);  //Кладёт сцену в окно.
        stage.show();  //Показывает окно пользователю.
    }

    public static void main(String[] args) {
        launch(args);
    }
}