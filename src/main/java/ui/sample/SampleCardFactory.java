package ui.sample;

import domain.Sample;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SampleService;

public class SampleCardFactory { //визуализация карточек образцов

    private final SampleService sampleService; //ссылка на сервис образцов
    private final Runnable afterChange; //поле хранит действие которое нужно выполнить после изменения образца

    public SampleCardFactory(SampleService sampleService, Runnable afterChange) {
        this.sampleService = sampleService;
        this.afterChange = afterChange;
    }

    public VBox createSampleCard(Sample sample) { //метод создания карточки
        VBox card = new VBox(6); //создание вертикального контейнера card, 6 px - расстояние между элементами внутри контейнера
        card.setPadding(new Insets(12)); //внутренние отступы
        card.setStyle(  //стили карточки
                "-fx-border-color: #F2BED1;" + //цвет рамки
                        "-fx-border-radius: 8;" + //скругление рамки
                        "-fx-background-radius: 8;" + //скругление фона
                        "-fx-background-color: #F9F5F6;" //цвет фона карточки
        );

        Label title = new Label("Sample #" + sample.getId()); //заголовок карточки
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); //стиль заголовка

        Label name = new Label("name: " + sample.getName()); //поля карточки
        Label type = new Label("type: " + sample.getType());
        Label location = new Label("location: " + sample.getLocation());
        Label status = new Label("status: " + sample.getStatus());
        Label owner = new Label("owner: " + sample.getOwnerUsername());

        HBox buttons = new HBox(8); //контейнер для кнопок, 8 расстояние между кнопками

        Button editButton = new Button("Edit"); //кнопка редактирования
        editButton.setOnAction(event ->
                SampleDialogs.showEditSampleDialog(sampleService, sample, afterChange)
        );

        Button archiveButton = new Button("Archive"); //кнопка архивирования
        archiveButton.setOnAction(event ->
                SampleDialogs.showArchiveSampleDialog(sampleService, sample, afterChange)
        );

        buttons.getChildren().addAll(editButton, archiveButton); //добавление кнопок в HBox

        card.getChildren().addAll( //добавление элементов в карточку
                title,
                name,
                type,
                location,
                status,
                owner,
                buttons
        );

        return card; //возврат готовой карточки
    }
}
