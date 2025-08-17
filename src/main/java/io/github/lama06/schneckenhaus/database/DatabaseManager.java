package io.github.lama06.schneckenhaus.database;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseManager {
    private final SchneckenhausPlugin plugin = SchneckenhausPlugin.INSTANCE;
    private final Logger logger = plugin.getSLF4JLogger();
    private Connection connection;

    public boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/Schneckenhaus/schneckenhaus.sqlite?foreign_keys=true");
        } catch (SQLException e) {
            logger.error("failed to open sqlite database", e);
            return false;
        }

        try {
            return executeTransaction(() -> {
                ResultSet dataVersionTable = connection.getMetaData().getTables(null, null, "data_version", null);
                if (!dataVersionTable.next()) {
                    createScheme();
                    connection.commit();
                    return true;
                }

                try (Statement statement = connection.createStatement()) {
                    ResultSet result = statement.executeQuery("SELECT data_version FROM data_version");
                    result.next();
                    PluginVersion version = PluginVersion.fromString(result.getString("data_version"));
                    upgrade(version);
                    connection.commit();
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error("failed to init database", e);
            return false;
        }
    }

    private void createScheme() throws SQLException, IOException {
        List<String> commands = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(
            getClass().getClassLoader().getResourceAsStream("scheme.sql"),
            StandardCharsets.UTF_8
        ))) {
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
        String updateVersionSql = """
            UPDATE data_version
            SET data_version = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(updateVersionSql)) {
            statement.setString(1, PluginVersion.current().toString());
            statement.executeUpdate();
        }
    }

    public <T> T executeTransaction(Transaction<T> transaction) throws Exception {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException();
        }

        try {
            connection.setAutoCommit(false);
            return transaction.run();
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
