package validation;

import domain.MeasurementParam;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtocolValidator {

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя протокола не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("имя протокола слишком длинное (макс. 128)");
        }
    }

    public static Set<MeasurementParam> parseParams(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("обязательные параметры не могут быть пустыми");
        }

        Set<MeasurementParam> params = Arrays.stream(raw.split(","))
                .map(String::trim)
                .peek(part -> {
                    if (part.isEmpty()) {
                        throw new IllegalArgumentException("между запятыми не должно быть пустых параметров");
                    }
                })
                .map(part -> {
                    try {
                        return MeasurementParam.valueOf(part.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("параметры должны быть из списка: PH, CONDUCTIVITY, TURBIDITY, NITRATE");
                    }
                })
                .collect(Collectors.toSet());

        validateParams(params);
        return params;
    }

    public static void validateParams(Set<?> params) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("нужно указать хотя бы один параметр");
        }
    }

    public static void validate(String name, Set<?> params) {
        validateName(name);
        validateParams(params);
    }
}