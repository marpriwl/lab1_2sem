package validation;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super("Ошибка валидации: " + message);
    }
}
