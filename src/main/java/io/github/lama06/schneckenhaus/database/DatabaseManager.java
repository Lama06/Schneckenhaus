package io.github.lama06.schneckenhaus.database;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.Bukkit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public final class DatabaseManager extends ConstantsHolder {
    private Connection connection;

    public boolean connect() {
        logger.info("opening sqlite database...");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/Schneckenhaus/schneckenhaus.sqlite?foreign_keys=true");
        } catch (SQLException e) {
            logger.error("failed to open sqlite database", e);
            return false;
        }

        try {
            executeTransaction(() -> {
                ResultSet dataVersionTable = connection.getMetaData().getTables(null, null, "data_version", null);
                if (!dataVersionTable.next()) {
                    createScheme();
                    return null;
                }

                try (Statement statement = connection.createStatement()) {
                    ResultSet result = statement.executeQuery("SELECT data_version FROM data_version");
                    result.next();
                    PluginVersion version = PluginVersion.fromString(result.getString("data_version"));
                    upgrade(version);
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("failed to init database", e);
            return false;
        }
    }

    private void createScheme() throws SQLException, IOException {
        logger.info("creating database scheme...");

        List<String> commands = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new BufferedInputStream(
            getClass().getClassLoader().getResourceAsStream("scheme.sql")
        ), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            int read;
            while ((read = reader.read()) != -1) {
                char character = (char) read;
                if (character == ';') {
                    commands.add(builder.toString());
                    builder.setLength(0);
                } else {
                    builder.append(character);
                }
            }
        }

        try (Statement statement = connection.createStatement()) {
            for (String command : commands) {
                statement.addBatch(command);
            }
            statement.executeBatch();
        }

        String insertDataVersionSql = """
            INSERT INTO data_version(data_version)
            VALUES (?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(insertDataVersionSql)) {
            statement.setString(1, PluginVersion.current().toString());
            statement.executeUpdate();
        }
    }

    private void upgrade(PluginVersion current) throws SQLException {
        if (PluginVersion.current().equals(current)) {
            return;
        }

        SortedMap<PluginVersion, FailableRunnable<SQLException>> upgrades = new TreeMap<>(Map.ofEntries(
            Map.entry(new PluginVersion(3, 0, 0), this::upgrade3_0_0)
        ));
        for (PluginVersion upgradeFromVersion : upgrades.tailMap(current).keySet()) {
            logger.info("upgrading database from version {}", upgradeFromVersion);
            upgrades.get(upgradeFromVersion).run();
        }

        String updateVersionSql = """
            UPDATE data_version
            SET data_version = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(updateVersionSql)) {
            statement.setString(1, PluginVersion.current().toString());
            statement.executeUpdate();
        }
    }

    private void upgrade3_0_0() throws SQLException {
        // during beta phase, 24.08.2025
        try (Statement statement = connection.createStatement()) {
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN name TEXT");
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN exit_position_x REAL");
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN exit_position_y REAL");
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN exit_position_z REAL");
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN exit_position_pitch REAL");
            statement.addBatch("ALTER TABLE shell_placements ADD COLUMN exit_position_yaw REAL");
            statement.executeBatch();
        }
    }

    public <T> T executeTransaction(Transaction<T> transaction) throws Exception {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("tried to execute database transaction async");
        }

        try {
            connection.setAutoCommit(false);
            T result = transaction.run();
            connection.commit();
            return result;
        } catch (Exception e) {
            logger.error("exception during database transaction", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                logger.error("failed to rollback database");
            }
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("failed to enable auto commit mode", e);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
