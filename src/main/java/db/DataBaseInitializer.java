package db;

import java.sql.Connection;
import java.sql.Statement;

public class DataBaseInitializer {

    public static void init() {
        try (
                Connection connection = DataBaseManager.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    login TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    created_at TIMESTAMPTZ NOT NULL,
                    updated_at TIMESTAMPTZ NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS samples (
                    id BIGSERIAL PRIMARY KEY,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    location TEXT NOT NULL,
                    status TEXT NOT NULL,
                    owner_id BIGINT NOT NULL REFERENCES users(id),
                    created_at TIMESTAMPTZ NOT NULL,
                    updated_at TIMESTAMPTZ NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS measurements (
                    id BIGSERIAL PRIMARY KEY,
                    sample_id BIGINT NOT NULL REFERENCES samples(id),
                    param TEXT NOT NULL,
                    value DOUBLE PRECISION NOT NULL,
                    unit TEXT NOT NULL,
                    method TEXT NOT NULL,
                    measured_at TIMESTAMPTZ NOT NULL,
                    owner_id BIGINT NOT NULL REFERENCES users(id),
                    created_at TIMESTAMPTZ NOT NULL,
                    updated_at TIMESTAMPTZ NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS protocols (
                    id BIGSERIAL PRIMARY KEY,
                    name TEXT NOT NULL,
                    required_params TEXT NOT NULL,
                    owner_id BIGINT NOT NULL REFERENCES users(id),
                    created_at TIMESTAMPTZ NOT NULL,
                    updated_at TIMESTAMPTZ NOT NULL
                )
            """);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Cannot initialize database: " + exception.getMessage(),
                    exception
            );
        }
    }
}
