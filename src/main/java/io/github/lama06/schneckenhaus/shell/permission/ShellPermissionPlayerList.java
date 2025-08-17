package io.github.lama06.schneckenhaus.shell.permission;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ShellPermissionPlayerList {
    private final Connection connection = SchneckenhausPlugin.INSTANCE.getDatabaseConnection();
    private final Logger logger = SchneckenhausPlugin.INSTANCE.getSLF4JLogger();

    private final int id;
    private final String name;
    private Set<UUID> players;

    public ShellPermissionPlayerList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Set<UUID> get() {
        if (players != null) {
            return players;
        }
        players = new HashSet<>();

        String sql = """
            SELECT player
            FROM shell_permissions
            WHERE id = ? AND name
            """.replace("name", name);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                UUID player = UUID.fromString(result.getString("player"));
                players.add(player);
            }
        } catch (SQLException e) {
            logger.error("failed to get shell permission list: {}", id, e);
        }

        return players;
    }

    public boolean contains(UUID player) {
        if (players != null) {
            return players.contains(player);
        }

        String sql = """
            SELECT 1
            FROM shell_permissions
            WHERE id = ? AND player = ? AND name
            """.replace("name", name);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, player.toString());
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            logger.error("failed to query player permission status: {}, {}", id, player, e);
            return false;
        }
    }

    public boolean contains(OfflinePlayer player) {
        return contains(player.getUniqueId());
    }

    public void set(UUID player, boolean value) {
        if (players != null) {
            if (value) {
                players.add(player);
            } else {
                players.remove(player);
            }
        }

        String sql = """
            INSERT INTO shell_permissions(id, player, name)
            VALUES (?, ?, ?)
            ON CONFLICT (id, player) DO UPDATE SET name = excluded.name
            """.replace("name", name);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, player.toString());
            statement.setBoolean(3, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to set player permission status: {}, {}", id, player, e);
        }
    }

    @Override
    public String toString() {
        return get().stream()
            .map(Bukkit::getOfflinePlayer)
            .map(OfflinePlayer::getName)
            .map(name -> Objects.requireNonNullElse(name, Message.UNKNOWN_PLAYER.toString()))
            .collect(Collectors.joining(", "));
    }
}
