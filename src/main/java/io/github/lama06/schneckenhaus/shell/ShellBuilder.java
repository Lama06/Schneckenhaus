package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.config.WorldConfig;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.util.ConcurrencyUtils;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ShellBuilder extends ConstantsHolder implements ShellData {
    private ShellCreationType creationType;
    private World world;
    private int position;
    private UUID creator;
    private UUID owner;
    private String name;
    private ShellPermissionMode enterPermissionMode;
    private ShellPermissionMode buildPermissionMode;

    public abstract ShellFactory getFactory();

    public final CompletableFuture<Shell> build() {
        return prepareBuild().handleAsync(
            (prepared, throwable) -> {
                if (throwable != null) {
                    logger.error("failed to prepare shell construction", throwable);
                    return null;
                }

                int id;
                try {
                    id = plugin.getDatabase().executeTransaction(() -> buildDuringTransaction(prepared));
                } catch (Exception e) {
                    logger.error("failed to build shell", e);
                    return null;
                }

                Shell shell = plugin.getShellManager().getShell(id);
                if (shell == null) {
                    return null;
                }
                shell.place();

                return shell;
            },
            ConcurrencyUtils::runOnMainThread
        ).exceptionally(e -> {
            logger.error("failed to build shell", e);
            return null;
        });
    }

    protected CompletableFuture<Object> prepareBuild() {
        return CompletableFuture.completedFuture(null);
    }

    protected int buildDuringTransaction(Object prepared) throws SQLException {
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
        if (!plugin.getShellManager().isShellWorld(world)) {
            throw new IllegalStateException("cannot create shell in this world: " + world);
        }

        if (position == 0) {
            String findPositionSql = """
                SELECT 1 AS priority, MIN(position)
                FROM unused_shell_positions
                WHERE world = ?
                UNION ALL
                SELECT 2 AS priority, MAX(position) + 1
                FROM shells
                WHERE world = ?
                UNION ALL
                SELECT 3 AS priority, 1
                ORDER BY priority
                """;
            try (PreparedStatement statement = connection.prepareStatement(findPositionSql)) {
                statement.setString(1, world.getName());
                statement.setString(2, world.getName());
                ResultSet result = statement.executeQuery();
                while (position == 0 && result.next()) {
                    position = result.getInt(2); // will return 0 if no position was found
                }
            }
        }
        String removeUnusedPositionSql = """
            DELETE FROM unused_shell_positions
            WHERE world = ? AND position = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(removeUnusedPositionSql)) {
            statement.setString(1, world.getName());
            statement.setInt(2, position);
            statement.executeUpdate();
        }

        if (owner == null) {
            owner = creator;
        }

        if (enterPermissionMode == null) {
            enterPermissionMode = plugin.getPluginConfig().getDefaultEnterPermissionMode();
        }

        if (buildPermissionMode == null) {
            buildPermissionMode = plugin.getPluginConfig().getDefaultBuildPermissionMode();
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
            statement.setString(i++, getFactory().getId());
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
                INSERT INTO home_shells(player, id)
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
