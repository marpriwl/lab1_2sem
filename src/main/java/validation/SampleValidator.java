package validation;

import domain.SampleStatus;

public class SampleValidator {

    public static void validate(String name, String type, String location) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("название не может быть пустым");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("название слишком длинное (макс. 128)");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("тип не может быть пустым");
        }
        if (type.length() > 64) {
            throw new IllegalArgumentException("тип слишком длинный (макс. 64)");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("местоположение не может быть пустым");
        }
        if (location.length() > 64) {
            throw new IllegalArgumentException("местоположение слишком длинное (макс. 64)");
        }
    }

    public static void validateStatus(String statusStr) {
        try {
            SampleStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("статус только ACTIVE или ARCHIVED");
        }
    }
}