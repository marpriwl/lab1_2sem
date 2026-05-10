package storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import domain.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserStorage {

    private final Path usersFilePath;
    private final ObjectMapper objectMapper;

    public UserStorage(Path usersFilePath) {
        this.usersFilePath = usersFilePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<User> load() {
        if (Files.notExists(usersFilePath)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    usersFilePath.toFile(),
                    new TypeReference<List<User>>() {
                    }
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Ошибка загрузки пользователей: " + exception.getMessage(), exception);
        }
    }

    public void save(List<User> users) {
        try {
            Path parent = usersFilePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writeValue(usersFilePath.toFile(), users);
        } catch (IOException exception) {
            throw new IllegalStateException("Ошибка сохранения пользователей: " + exception.getMessage(), exception);
        }
    }
}
