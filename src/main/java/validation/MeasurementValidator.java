package validation;

public class MeasurementValidator {

    public static void validate(double value, String unit, String method) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("значение должно быть числом");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("единицы не могут быть пустыми");
        }
        if (unit.length() > 16) {
            throw new IllegalArgumentException("единицы слишком длинные (макс. 16)");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("метод не может быть пустым");
        }
        if (method.length() > 64) {
            throw new IllegalArgumentException("метод слишком длинный (макс. 64)");
        }
    }
}