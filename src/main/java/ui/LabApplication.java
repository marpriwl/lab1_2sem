package ui;

import domain.Sample;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.MeasurementService;
import service.ProtocolService;
import service.SampleService;

import ui.sample.SampleDialogs;
import ui.sample.SampleCardFactory;

public class LabApplication extends Application {   //Application — базовый класс JavaFX-приложения.

    private final SampleService sampleService = new SampleService();
    private final MeasurementService measurementService = new MeasurementService(sampleService);
    private final ProtocolService protocolService = new ProtocolService();

    private SampleCardFactory sampleCardFactory;

    private VBox cardsBox;  //VBox — это вертикальный контейнер JavaFX. Он размещает элементы сверху вниз. СardsBox нужен, чтобы хранить карточки

    @Override
    public void start(Stage stage) {  //Если класс наследуется от Application, JavaFX ожидает, что в нём будет метод: start(Stage stage). Это главный метод JavaFX. Он вызывается автоматически при запуске приложения. Stage — это главное окно.

        sampleCardFactory = new SampleCardFactory(sampleService, this::refreshSamples);

        BorderPane root = new BorderPane(); //BorderPane — контейнер с зонами.
        root.setPadding(new Insets(12)); //Добавляет внутренний отступ 12 px со всех сторон.

        Label title = new Label("Lab Manager"); //Label — текстовая надпись.
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;"); //setStyle(...) задает CSS-стиль.

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

        Button addSampleButton = new Button("Add Sample"); //Создается кнопка Add Sample.
        addSampleButton.setOnAction(event ->  // setOnAction(...) задаёт действие при нажатии: открывается диалог добавления образца.
                SampleDialogs.showAddSampleDialog(sampleService, this::refreshSamples)
        );

        Button refreshButton = new Button("Refresh");  //Создается кнопка Refresh.
        refreshButton.setOnAction(event -> refreshSamples()); //Действие при нажатии (вызывается метод refreshSamples())

        HBox actionsPanel = new HBox(10);  //Это горизонтальная панель под верхним меню.
        actionsPanel.getChildren().addAll(addSampleButton, refreshButton);
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        VBox topContainer = new VBox(8);  //topContainer — общий верхний вертикальный контейнер, который содержит. первая строка:  Lab Manager + верхние кнопки; вторая строка: Add Sample + Refresh
        topContainer.getChildren().addAll(topPanel, actionsPanel);

        cardsBox = new VBox(10);  //cardsBox — вертикальный список карточек. Расстояние между карточками 10 px.
        cardsBox.setPadding(new Insets(10));  //Отступ внутри — 10 px.

        ScrollPane scrollPane = new ScrollPane(cardsBox);  //ScrollPane — контейнер с прокруткой.
        scrollPane.setFitToWidth(true);  //Означает: содержимое растягивается по ширине ScrollPane.

        root.setTop(topContainer);  //Размещение в BorderPane. Верхняя часть — topContainer.
        root.setCenter(scrollPane);  //Центральная часть — scrollPane.

        refreshSamples();  //Первичное обновление карточек (метод вызывается сразу после запуска).

        Scene scene = new Scene(root, 900, 600);  //Создание сцены: Scene — содержимое окна; root — главный контейнер; 900, 600 — размеры окна.

        stage.setTitle("Lab Manager");  //Настройка окна. Заголовок окна.
        stage.setScene(scene);  //Кладёт сцену в окно.
        stage.show();  //Показывает окно пользователю.
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

    public static void main(String[] args) {
        launch(args);
    }
}