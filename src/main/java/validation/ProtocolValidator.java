package validation;

import java.util.Set;

public class ProtocolValidator {

    public static void validate(String name, Set<?> params) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя протокола не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("имя протокола слишком длинное (макс. 128)");
        }
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("нужно указать хотя бы один параметр");
        }
    }
}