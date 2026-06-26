package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Path CONFIG_PATH = Path.of("db.properties");

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();

        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Cannot read db.properties: " + exception.getMessage(),
                        exception
                );
            }
        }

        String url = firstNotBlank(
                System.getenv("LAB_DB_URL"),
                properties.getProperty("db.url")
        );

        if (url == null) {
            throw new IllegalStateException(
                    "Database is not configured. Set LAB_DB_URL or create db.properties."
            );
        }

        String user = firstNotBlank(
                System.getenv("LAB_DB_USER"),
                properties.getProperty("db.user")
        );

        String password = firstNotBlank(
                System.getenv("LAB_DB_PASSWORD"),
                properties.getProperty("db.password")
        );

        return new DatabaseConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static String firstNotBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }

        if (second != null && !second.isBlank()) {
            return second.trim();
        }

        return null;
    }
}
