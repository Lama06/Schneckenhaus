package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.WorldConfig;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public abstract class ShellBuilder implements ShellData {
    protected final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
    protected final Connection connection = SchneckenPlugin.INSTANCE.getDBConnection();
    protected final Logger logger = SchneckenPlugin.INSTANCE.getSLF4JLogger();

    private ShellCreationType creationType;
    private World world;
    private int position;
    private UUID creator;
    private UUID owner;
    private String name;
    private ShellPermissionMode enterPermissionMode;
    private ShellPermissionMode buildPermissionMode;

    public abstract ShellFactory getFactory();

    public final Shell build() {
        Connection connection = SchneckenPlugin.INSTANCE.getDBConnection();
        int id;
        try {
            connection.setAutoCommit(false);
            id = buildDuringTransaction();
        } catch (Exception e) {
            logger.error("failed to build snail shell", e);
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                logger.error("failed to rollback failed shell insertion", rollbackException);
            }
            return null;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("failed to disable auto commit mode", e);
            }
        }
        Shell shell = plugin.getShellManager().getShell(id);
        shell.placeInitially();
        return shell;
    }

    protected int buildDuringTransaction() throws SQLException {
        Map<String, WorldConfig> worlds = plugin.getPluginConfig().getWorlds();
        if (world == null) {
            for (String name : worlds.keySet()) {
                if (worlds.get(name).getConditions().stream().anyMatch(condition -> condition.check(this))) {
                    world = Bukkit.getWorld(name);
                    if (world != null) {
                        break;
                    }
                }
            }
        }
        if (world == null) {
            for (String name : worlds.keySet()) {
                if (worlds.get(name).isFallback()) {
                    world = Bukkit.getWorld(name);
                }
            }
        }
        if (world == null) {
            throw new IllegalStateException("no world found to create snail shell");
        }

        if (position == 0) {
            String findPositionSql = """
                SELECT position + 1 AS position
                FROM shells AS x
                WHERE world = ? AND NOT EXISTS (
                    SELECT 1
                    FROM shells
                    WHERE world = ? AND position = x.position + 1
                )
                """;
            try (PreparedStatement statement = connection.prepareStatement(findPositionSql)) {
                statement.setString(1, world.getName());
                statement.setString(2, world.getName());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    position = result.getInt("position");
                } else {
                    position = 1;
                }
            }
        }

        String insertSql = """
            INSERT INTO shells(world, position, creator, creation_type, type, name, enter_permission_mode, build_permission_mode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            int i = 1;
            statement.setString(i++, world.getName());
            statement.setInt(i++, position);
            statement.setString(i++, creator == null ? null : creator.toString());
            statement.setString(i++, creationType.name().toLowerCase(Locale.ROOT));
            statement.setString(i++, getFactory().getName());
            statement.setString(i++, name);
            statement.setString(i++, enterPermissionMode.name().toLowerCase(Locale.ROOT));
            statement.setString(i++, buildPermissionMode.name().toLowerCase(Locale.ROOT));
            statement.executeUpdate();
        }

        int id;
        String selectIdSql = """
            SELECT MAX(id)
            FROM shells
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(selectIdSql);
            result.next();
            id = result.getInt(1);
        }

        String permissionSql = """
            INSERT INTO shell_permissions(id, player, owner)
            VALUES (?, ?, TRUE)
            """;
        try (PreparedStatement statement = connection.prepareStatement(permissionSql)) {
            statement.setInt(1, id);
            statement.setString(2, owner.toString());
            statement.executeUpdate();
        }

        if (creationType == ShellCreationType.HOME) {
            String homeSql = """
                INSERT INTO home_shells(player, shell_id)
                VALUES (?, ?)
                """;
            try (PreparedStatement statement = connection.prepareStatement(homeSql)) {
                statement.setString(1, creator.toString());
                statement.setInt(2, id);
                statement.executeUpdate();
            }
        }

        return id;
    }

    public ShellCreationType getCreationType() {
        return creationType;
    }

    public void setCreationType(ShellCreationType creationType) {
        this.creationType = creationType;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public UUID getCreator() {
        return creator;
    }

    public void setCreator(UUID creator) {
        this.creator = creator;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShellPermissionMode getEnterPermissionMode() {
        return enterPermissionMode;
    }

    public void setEnterPermissionMode(ShellPermissionMode enterPermissionMode) {
        this.enterPermissionMode = enterPermissionMode;
    }

    public ShellPermissionMode getBuildPermissionMode() {
        return buildPermissionMode;
    }

    public void setBuildPermissionMode(ShellPermissionMode buildPermissionMode) {
        this.buildPermissionMode = buildPermissionMode;
    }
}
