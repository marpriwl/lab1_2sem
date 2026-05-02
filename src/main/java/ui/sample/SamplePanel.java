package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SampleService;

public class SamplePanel extends VBox {  //VBox — контейнер JavaFX, который располагает элементы вертикально.

    private final SampleService sampleService;
    private final SampleCardFactory sampleCardFactory;

    private final VBox cardsBox = new VBox(10); //контейнер для карточек

    public SamplePanel(SampleService sampleService) {
        this.sampleService = sampleService;
        this.sampleCardFactory = new SampleCardFactory(sampleService, this::refreshSamples);

        setSpacing(10); //Эта строка задаёт расстояние между основными элементами панели.
        setPadding(new Insets(0)); //внутр отступы

        Button addSampleButton = new Button("Add Sample");
        addSampleButton.setOnAction(event ->
                SampleDialogs.showAddSampleDialog(sampleService, this::refreshSamples)
        );

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshSamples());

        HBox actionsPanel = new HBox(10); //Создаётся горизонтальный контейнер для кнопок.
        actionsPanel.getChildren().addAll(addSampleButton, refreshButton);
        actionsPanel.setPadding(new Insets(0, 0, 12, 0));

        cardsBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(cardsBox); //прокрутка
        scrollPane.setFitToWidth(true);

        getChildren().addAll(actionsPanel, scrollPane); //Добавление элементов в саму панель

        refreshSamples(); //Первичное обновление списка
    }

    private void refreshSamples() { //метод обновления списка образцов
        cardsBox.getChildren().clear();

        if (sampleService.getAll().isEmpty()) { //проверка наличия карточек
            Label emptyLabel = new Label("No samples yet");
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Sample sample : sampleService.getAll().values()) { //создание карточек
            VBox card = sampleCardFactory.createSampleCard(sample);
            cardsBox.getChildren().add(card);
        }
    }

    public void refresh() { //публичный метод обновления для других классов
        refreshSamples();
    }
}
