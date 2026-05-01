package ui.common;

import javafx.scene.control.Alert;  //Alert — это стандартное всплывающее окно JavaFX.

public class AlertUtils {  //AlertUtils — это вспомогательный класс для показа ошибок в JavaFX.

    private AlertUtils() {
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);  //Создаем окно ошибки
        alert.setTitle("Error"); //Заголовок окна
        alert.setHeaderText("Operation failed"); //Крупный заголовок внутри окна
        alert.setContentText(message);  //Основной текст ошибки (от валидатора)
        alert.showAndWait(); //Показать окно и ждать закрытия
    }
}