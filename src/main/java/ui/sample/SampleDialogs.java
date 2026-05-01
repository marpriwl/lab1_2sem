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
import ui.common.AlertUtils;
import validation.SampleValidator;

import java.util.Optional;

public class SampleDialogs {

    private SampleDialogs() {
    }

    public static void showAddSampleDialog(SampleService sampleService, Runnable afterSuccess) {
        Dialog<ButtonType> dialog = new Dialog<>();  //Создаем диалоговое окно
        dialog.setTitle("Add Sample");  //Заголовок окна
        dialog.setHeaderText("Create new sample");  //Заголовок внутри окна

        TextField nameField = new TextField();  //Создаем поле ввода названия образца
        nameField.setPromptText("Name"); //Подсказка

        TextField typeField = new TextField(); //Создаем поле ввода типа образца
        typeField.setPromptText("Type");  //Подсказка

        TextField locationField = new TextField();  //Создаем поле ввода местоположения образца
        locationField.setPromptText("Location");  //Подсказка

        GridPane form = createSampleForm(nameField, typeField, locationField); //Создаем форму из трех полей (общий вспомогательный метод)

        dialog.getDialogPane().setContent(form);  //Кладем форму внутрь диалога
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);  //Создаем кнопки OK и Cancel

        Optional<ButtonType> result = dialog.showAndWait(); //Показываем диалог и ждем закрытия

        if (result.isPresent() && result.get() == ButtonType.OK) { //Проверяем что результат есть и пользователь нажал ОК
            try {
                String name = nameField.getText().trim(); //убираем пробелы о краям
                String type = typeField.getText().trim(); //убираем пробелы о краям
                String location = locationField.getText().trim(); //убираем пробелы о краям

                sampleService.add(name, type, location);  //создание образца через сервис
                afterSuccess.run(); //после успешного создания вызываем обновление карточек
            } catch (Exception e) {  //ловим ошибку
                AlertUtils.showError(e.getMessage()); //обращаемся к Alert
            }
        }
    }

    public static void showEditSampleDialog(SampleService sampleService, Sample sample, Runnable afterSuccess) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Sample");
        dialog.setHeaderText("Edit sample #" + sample.getId());

        TextField nameField = new TextField(sample.getName()); //создаем поля ввода и заполняем их текущими значениями
        TextField typeField = new TextField(sample.getType());
        TextField locationField = new TextField(sample.getLocation());

        GridPane form = createSampleForm(nameField, typeField, locationField);  //Создаём такую же форму, как при добавлении, но поля уже заполнены.

        dialog.getDialogPane().setContent(form); //кладем форму в диалог
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL); //кнопки

        Optional<ButtonType> result = dialog.showAndWait();  //показываем диалог и ждем

        if (result.isPresent() && result.get() == ButtonType.OK) {  //продолжаем только если пользователь нажал окей
            try {  //блок обработки возможной ошибки
                String name = nameField.getText().trim(); //считываем новые значения полей
                String type = typeField.getText().trim();
                String location = locationField.getText().trim();

                SampleValidator.validate(name, type, location);

                sampleService.update(sample.getId(), "name", name);  //обновление полей через сервис
                sampleService.update(sample.getId(), "type", type);
                sampleService.update(sample.getId(), "location", location);

                afterSuccess.run(); //после успешного ввода обновляем ui
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage()); //показ ошибки через alert
            }
        }
    }

    public static void showArchiveSampleDialog(SampleService sampleService, Sample sample, Runnable afterSuccess) {
        Dialog<ButtonType> dialog = new Dialog<>();  //создаем диалог
        dialog.setTitle("Archive Sample");
        dialog.setHeaderText("Archive sample #" + sample.getId());

        Label message = new Label(  //создаем текст для подтверждения
                "Are you sure you want to archive this sample?\n\n" +
                        "name: " + sample.getName() + "\n" +
                        "type: " + sample.getType() + "\n" +
                        "location: " + sample.getLocation()
        );

        dialog.getDialogPane().setContent(message); //кладем текст в диалог

        ButtonType archiveButton = new ButtonType("Archive", ButtonBar.ButtonData.OK_DONE); //создаем свою кнопку архивировать (указываем для javafx, что это подтверждающая кнопка)
        dialog.getDialogPane().getButtonTypes().addAll(archiveButton, ButtonType.CANCEL); //add buttons

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == archiveButton) {
            try {
                sampleService.archive(sample.getId()); //архивируем образец через сервис
                afterSuccess.run();
            } catch (Exception e) {
                AlertUtils.showError(e.getMessage());
            }
        }
    }

    private static GridPane createSampleForm(  //вспомогательный метод
            TextField nameField,
            TextField typeField,
            TextField locationField
    ) {
        GridPane form = new GridPane(); //создаем сетку
        form.setHgap(10); //горизонтальный промежуток между колонками
        form.setVgap(10); //вертикальный между строками
        form.setPadding(new Insets(10)); //внутренний отступ

        form.add(new Label("Name:"), 0, 0); //подпись name
        form.add(nameField, 1, 0); //поле ввода

        form.add(new Label("Type:"), 0, 1); //подпись
        form.add(typeField, 1, 1); //поле ввода

        form.add(new Label("Location:"), 0, 2); //подпись
        form.add(locationField, 1, 2); //поле ввода

        return form; //возвращаем готовую форму
    }
}