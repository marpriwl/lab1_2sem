package validation;

import domain.MeasurementParam;

public class MeasurementValidator {

    public static MeasurementParam validateParam(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("параметр не может быть пустым");
        }

        try {
            return MeasurementParam.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("параметр должен быть одним из: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
        }
    }

    public static double validateValue(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("значение не может быть пустым");
        }

        double value;
        try {
            value = Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("значение должно быть числом");
        }

        validateValue(value);
        return value;
    }

    public static void validateValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("значение должно быть числом");
        }
    }

    public static void validateUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("единицы не могут быть пустыми");
        }
        if (unit.length() > 16) {
            throw new IllegalArgumentException("единицы слишком длинные (макс. 16)");
        }
    }

    public static void validateMethod(String method) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("метод не может быть пустым");
        }
        if (method.length() > 64) {
            throw new IllegalArgumentException("метод слишком длинный (макс. 64)");
        }
    }

    public static void validate(double value, String unit, String method) {
        validateValue(value);
        validateUnit(unit);
        validateMethod(method);
    }
}