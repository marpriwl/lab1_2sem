package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage {
    private final ObjectMapper mapper;

    public FileStorage() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Path path, LabData data) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            mapper.writeValue(path.toFile(), data);
        } catch (IOException e) {
            throw new IllegalArgumentException("не удалось сохранить файл: " + e.getMessage());
        }
    }

    public LabData load(Path path) {
        try {
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("файл не найден: " + path);
            }

            if (!Files.isRegularFile(path)) {
                throw new IllegalArgumentException("путь не является файлом: " + path);
            }

            return mapper.readValue(path.toFile(), LabData.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("не удалось загрузить файл: " + e.getMessage());
        }
    }
}