package storage;

import db.DataBaseManager;
import domain.Measurement;
import domain.MeasurementParam;
import domain.Protocol;
import domain.Sample;
import domain.SampleStatus;
import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DbStorage {

    public User insertUser(User user) {
        String sql = """
                INSERT INTO users (login, password_hash, created_at, updated_at)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPasswordHash());
            setInstant(stmt, 3, user.getCreatedAt());
            setInstant(stmt, 4, user.getUpdatedAt());
            stmt.executeUpdate();

            user.setId(readGeneratedId(stmt));
            return user;
        } catch (SQLException exception) {
            throw databaseError("register user", exception);
        }
    }

    public List<User> findAllUsers() {
        String sql = """
                SELECT id, login, password_hash, created_at, updated_at
                FROM users
                ORDER BY id
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            List<User> users = new ArrayList<>();

            while (rs.next()) {
                users.add(readUser(rs));
            }

            return users;
        } catch (SQLException exception) {
            throw databaseError("load users", exception);
        }
    }

    public Sample insertSample(Sample sample) {
        String sql = """
                INSERT INTO samples (
                    name, type, location, status, owner_id, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, sample.getName());
            stmt.setString(2, sample.getType());
            stmt.setString(3, sample.getLocation());
            stmt.setString(4, sample.getStatus().name());
            stmt.setLong(5, sample.getOwnerId());
            setInstant(stmt, 6, sample.getCreatedAt());
            setInstant(stmt, 7, sample.getUpdatedAt());
            stmt.executeUpdate();

            sample.setId(readGeneratedId(stmt));
            return sample;
        } catch (SQLException exception) {
            throw databaseError("insert sample", exception);
        }
    }

    public void updateSample(Sample sample) {
        String sql = """
                UPDATE samples
                SET name = ?, type = ?, location = ?, status = ?, updated_at = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, sample.getName());
            stmt.setString(2, sample.getType());
            stmt.setString(3, sample.getLocation());
            stmt.setString(4, sample.getStatus().name());
            setInstant(stmt, 5, sample.getUpdatedAt());
            stmt.setLong(6, sample.getId());
            int updatedRows = stmt.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException("Sample id=" + sample.getId() + " was not found in DB");
            }
        } catch (SQLException exception) {
            throw databaseError("update sample", exception);
        }
    }

    public void deleteSample(long id) {
        String sql = "DELETE FROM samples WHERE id = ?";
        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException exception) {
            throw databaseError("delete sample", exception);
        }
    }

    public void deleteMeasurement(long id) {
        String sql = "DELETE FROM measurements WHERE id = ?";
        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException exception) {
            throw databaseError("delete measurement", exception);
        }
    }

    public void deleteProtocol(long id) {
        String sql = "DELETE FROM protocols WHERE id = ?";
        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException exception) {
            throw databaseError("delete protocol", exception);
        }
    }

    public Measurement insertMeasurement(Measurement measurement) {
        String sql = """
                INSERT INTO measurements (
                    sample_id, param, value, unit, method,
                    measured_at, owner_id, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setLong(1, measurement.getSampleId());
            stmt.setString(2, measurement.getParam().name());
            stmt.setDouble(3, measurement.getValue());
            stmt.setString(4, measurement.getUnit());
            stmt.setString(5, measurement.getMethod());
            setInstant(stmt, 6, measurement.getMeasuredAt());
            stmt.setLong(7, measurement.getOwnerId());
            setInstant(stmt, 8, measurement.getCreatedAt());
            setInstant(stmt, 9, measurement.getUpdatedAt());
            stmt.executeUpdate();

            measurement.setId(readGeneratedId(stmt));
            return measurement;
        } catch (SQLException exception) {
            throw databaseError("insert measurement", exception);
        }
    }

    public Protocol insertProtocol(Protocol protocol) {
        String sql = """
                INSERT INTO protocols (
                    name, required_params, owner_id, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DataBaseManager.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, protocol.getName());
            stmt.setString(2, paramsToString(protocol.getRequiredParams()));
            stmt.setLong(3, protocol.getOwnerId());
            setInstant(stmt, 4, protocol.getCreatedAt());
            setInstant(stmt, 5, protocol.getUpdatedAt());
            stmt.executeUpdate();

            protocol.setId(readGeneratedId(stmt));
            return protocol;
        } catch (SQLException exception) {
            throw databaseError("insert protocol", exception);
        }
    }

    public LabData loadLabData() {
        try (Connection connection = DataBaseManager.getConnection()) {
            return new LabData(
                    loadSamples(connection),
                    loadMeasurements(connection),
                    loadProtocols(connection)
            );
        } catch (SQLException exception) {
            throw databaseError("load lab data", exception);
        }
    }

    private List<Sample> loadSamples(Connection connection) throws SQLException {
        String sql = """
                SELECT id, name, type, location, status, owner_id, created_at, updated_at
                FROM samples
                ORDER BY id
                """;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            List<Sample> samples = new ArrayList<>();

            while (rs.next()) {
                samples.add(readSample(rs));
            }

            return samples;
        }
    }

    private List<Measurement> loadMeasurements(Connection connection) throws SQLException {
        String sql = """
                SELECT id, sample_id, param, value, unit, method,
                       measured_at, owner_id, created_at, updated_at
                FROM measurements
                ORDER BY id
                """;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            List<Measurement> measurements = new ArrayList<>();

            while (rs.next()) {
                measurements.add(readMeasurement(rs));
            }

            return measurements;
        }
    }

    private List<Protocol> loadProtocols(Connection connection) throws SQLException {
        String sql = """
                SELECT id, name, required_params, owner_id, created_at, updated_at
                FROM protocols
                ORDER BY id
                """;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            List<Protocol> protocols = new ArrayList<>();

            while (rs.next()) {
                protocols.add(readProtocol(rs));
            }

            return protocols;
        }
    }

    private User readUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setCreatedAt(getInstant(rs, "created_at"));
        user.setUpdatedAt(getInstant(rs, "updated_at"));
        return user;
    }

    private Sample readSample(ResultSet rs) throws SQLException {
        Sample sample = new Sample();
        sample.setId(rs.getLong("id"));
        sample.setName(rs.getString("name"));
        sample.setType(rs.getString("type"));
        sample.setLocation(rs.getString("location"));
        sample.setStatus(SampleStatus.valueOf(rs.getString("status")));
        sample.setOwnerId(rs.getLong("owner_id"));
        sample.setCreatedAt(getInstant(rs, "created_at"));
        sample.setUpdatedAt(getInstant(rs, "updated_at"));
        return sample;
    }

    private Measurement readMeasurement(ResultSet rs) throws SQLException {
        Measurement measurement = new Measurement();
        measurement.setId(rs.getLong("id"));
        measurement.setSampleId(rs.getLong("sample_id"));
        measurement.setParam(MeasurementParam.valueOf(rs.getString("param")));
        measurement.setValue(rs.getDouble("value"));
        measurement.setUnit(rs.getString("unit"));
        measurement.setMethod(rs.getString("method"));
        measurement.setMeasuredAt(getInstant(rs, "measured_at"));
        measurement.setOwnerId(rs.getLong("owner_id"));
        measurement.setCreatedAt(getInstant(rs, "created_at"));
        measurement.setUpdatedAt(getInstant(rs, "updated_at"));
        return measurement;
    }

    private Protocol readProtocol(ResultSet rs) throws SQLException {
        Protocol protocol = new Protocol();
        protocol.setId(rs.getLong("id"));
        protocol.setName(rs.getString("name"));
        protocol.setRequiredParams(paramsFromString(rs.getString("required_params")));
        protocol.setOwnerId(rs.getLong("owner_id"));
        protocol.setCreatedAt(getInstant(rs, "created_at"));
        protocol.setUpdatedAt(getInstant(rs, "updated_at"));
        return protocol;
    }

    private void setInstant(PreparedStatement stmt, int index, Instant instant)
            throws SQLException {
        Instant value = instant == null ? Instant.now() : instant;
        stmt.setObject(index, OffsetDateTime.ofInstant(value, ZoneOffset.UTC));
    }

    private Instant getInstant(ResultSet rs, String column) throws SQLException {
        try {
            OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);

            if (value != null) {
                return value.toInstant();
            }
        } catch (SQLException ignored) {
            Timestamp timestamp = rs.getTimestamp(column);

            if (timestamp != null) {
                return timestamp.toInstant();
            }
        }

        return null;
    }

    private long readGeneratedId(PreparedStatement stmt) throws SQLException {
        try (ResultSet keys = stmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getLong(1);
            }
        }

        throw new SQLException("Database did not return generated id");
    }

    private String paramsToString(Set<MeasurementParam> params) {
        return params.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private Set<MeasurementParam> paramsFromString(String raw) {
        EnumSet<MeasurementParam> params = EnumSet.noneOf(MeasurementParam.class);

        if (raw == null || raw.isBlank()) {
            return params;
        }

        for (String part : raw.split(",")) {
            params.add(MeasurementParam.valueOf(part.trim()));
        }

        return params;
    }

    private RuntimeException databaseError(String action, SQLException exception) {
        if ("23505".equals(exception.getSQLState())) {
            return new IllegalArgumentException(
                    "Database constraint error while trying to " + action + ": duplicate value",
                    exception
            );
        }

        if ("23503".equals(exception.getSQLState())) {
            return new IllegalArgumentException(
                    "Database constraint error while trying to " + action + ": referenced object does not exist",
                    exception
            );
        }

        return new IllegalStateException(
                "Database error while trying to " + action + ": " + exception.getMessage(),
                exception
        );
    }
}
