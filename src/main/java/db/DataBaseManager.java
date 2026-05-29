package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseManager {

    public static Connection getConnection()
            throws SQLException {
        DatabaseConfig config = DatabaseConfig.load();

        if (config.getUser() == null && config.getPassword() == null) {
            return DriverManager.getConnection(config.getUrl());
        }

        Properties properties = new Properties();

        if (config.getUser() != null) {
            properties.setProperty("user", config.getUser());
        }

        if (config.getPassword() != null) {
            properties.setProperty("password", config.getPassword());
        }

        return DriverManager.getConnection(config.getUrl(), properties);
    }
}
