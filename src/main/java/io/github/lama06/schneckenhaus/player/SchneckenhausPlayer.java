package io.github.lama06.schneckenhaus.player;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.position.Position;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SchneckenhausPlayer {
    private final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
    private final SchneckenhausConfig config = plugin.getPluginConfig();
    private final Connection connection = plugin.getDBConnection();
    private final Logger logger = plugin.getSLF4JLogger();

    private final Player player;

    public SchneckenhausPlayer(Player player) {
        this.player = player;
    }

    public boolean isInside(Shell shell, boolean direct, boolean nesting) {
        Position position = Position.location(player.getLocation());
        if (position == null) {
            return false;
        }

        if (nesting) {
            List<Location> previousLocations = getPreviousLocations();
            for (Location previousLocation : previousLocations) {
                Position previousPosition = Position.location(previousLocation);
                if (previousPosition == null) {
                    continue;
                }
                if (previousPosition.equals(position)) {
                    return true;
                }
            }
        }

        return direct && shell.getPosition().equals(position);
    }

    public boolean isInside(Shell shell) {
        return isInside(shell, true, true);
    }

    public void enter(Shell shell, ShellTeleportOptions options) {
        if (isInside(shell)) {
            return;
        }
        if (!Permission.ENTER_SHELL.require(player)) {
            return;
        }
        List<Location> previousLocations = getPreviousLocations();
        if (!previousLocations.isEmpty() && !Permission.ENTER_NESTED_SHELLS.check(player)) {
            return;
        }
        if (!shell.getEnterPermission().hasPermission(player)) {
            player.sendMessage(Message.ERROR_ENTER_PERMISSION.asComponent(NamedTextColor.RED));
            return;
        }

        if (previousLocations.isEmpty() || options.isStorePreviousPositionWhenNesting()) {
            pushPreviousLocation(player.getLocation());
        }

        player.teleport(shell.getSpawnLocation());

        if (shell.getName() != null) {
            player.showTitle(Title.title(Component.text(shell.getName()), Component.empty()));
        }
        if (options.isPlaySound()) {
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 1);
        }

        String incrementStatisticsSql = """
            INSERT INTO shell_access_statistics(shell, player, amount)
            VALUES (?, ?, 1)
            ON CONFLICT (shell, player) DO UPDATE SET amount = amount + 1
            """;
        try (PreparedStatement statement = connection.prepareStatement(incrementStatisticsSql)) {
            statement.setInt(1, shell.getId());
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to increment shell access statistics: {}, {}", player, shell.getId(), e);
        }
    }

    public void leave() {
        Shell shell = plugin.getShellManager().getShellAt(player);
        if (shell == null) {
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);

        if (leaveToLastLocation()) {
            return;
        }
        if (leaveToShellPlacement(shell)) {
            return;
        }
        if (leaveToFallbackPosition()) {
            return;
        }
        if (leaveToSpawnpoint()) {
            return;
        }

        player.sendMessage(Component.text("No world found to teleport to", NamedTextColor.RED));
    }

    private boolean leaveToLastLocation() {
        Location location = popPreviousLocation();
        if (location == null) {
            return false;
        }
        player.teleport(location);
        return true;
    }

    private boolean leaveToShellPlacement(Shell shell) {
        String sql = """
            SELECT world, x, y, z
            FROM shell_placements
            WHERE id = ?
            ORDER BY
                (CASE WHEN placed_by = ? THEN 0 ELSE 1 END) ASC,
                time DESC
            LIMIT 1
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shell.getId());
            statement.setString(2, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return false;
            }
            World world = Bukkit.getWorld(result.getString("world"));
            if (world == null) {
                return false;
            }
            Block block = world.getBlockAt(result.getInt("x"), result.getInt("y"), result.getInt("z"));
            player.teleport(block.getLocation().toCenterLocation().add(0, 1, 0));
            return true;
        } catch (SQLException e) {
            logger.error("failed to query latest shell placement: {}", shell.getId(), e);
            return false;
        }
    }

    private boolean leaveToFallbackPosition() {
        Location location = config.getFallbackExitLocation();
        if (location == null) {
            return false;
        }
        player.teleport(location);
        return true;
    }

    private boolean leaveToSpawnpoint() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            player.teleport(world.getSpawnLocation());
            return true;
        }
        world = Bukkit.getWorlds().stream()
            .filter(otherWorld -> !plugin.getPluginConfig().getWorlds().containsKey(otherWorld.getName()))
            .findAny()
            .orElse(null);
        if (world == null) {
            return false;
        }
        player.teleport(world.getSpawnLocation());
        return true;
    }

    public List<Location> getPreviousLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = """
            SELECT world, x, y, z, yaw, pitch
            FROM previous_player_locations
            WHERE player = ?
            ORDER BY time
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                World world = Bukkit.getWorld(result.getString("world"));
                if (world == null) {
                    continue;
                }
                locations.add(new Location(
                    world,
                    result.getDouble("x"),
                    result.getDouble("y"),
                    result.getDouble("z"),
                    result.getFloat("yaw"),
                    result.getFloat("pitch")
                ));
            }
        } catch (SQLException e) {
            logger.error("failed to query previous player locations: {}", player);
        }
        return locations;
    }

    public void pushPreviousLocation(Location location) {
        String sql = """
            INSERT INTO previous_player_locations(player, world, x, y, z, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, location.getWorld().getName());
            statement.setDouble(3, location.getX());
            statement.setDouble(4, location.getY());
            statement.setDouble(5, location.getZ());
            statement.setFloat(6, location.getYaw());
            statement.setFloat(7, location.getPitch());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to push previous player location: {}", player, e);
        }
    }

    public Location popPreviousLocation() {
        String sql = """
            DELETE FROM previous_player_locations
            WHERE player = ? AND time IN (
                SELECT MAX(time)
                FROM previous_player_locations
                WHERE player = ?
            )
            RETURNING world, x, y, z, pitch, yaw
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            World world = Bukkit.getWorld(result.getString("world"));
            if (world == null) {
                return null;
            }
            return new Location(
                world,
                result.getDouble("x"),
                result.getDouble("y"),
                result.getDouble("z"),
                result.getFloat("yaw"),
                result.getFloat("pitch")
            );
        } catch (SQLException e) {
            logger.error("failed to pop previous player location: {}", player);
            return null;
        }
    }

    public void clearPreviousLocations() {
        String sql = """
            DELETE FROM previous_player_locations
            WHERE player = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to clear previous player locations: {}", player, e);
        }
    }

    public Shell getHomeShell() {
        String sql = """
            SELECT home_id
            FROM home_shells
            WHERE player = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            return plugin.getShellManager().getShell(result.getInt("home_id"));
        } catch (SQLException e) {
            logger.error("failed to query player's home shell: {}", player, e);
            return null;
        }
    }

    public void setHomeShell(int id) {
        String sql = """
            INSERT INTO home_shells(player, home_id)
            VALUES (?, ?)
            ON CONFLICT (player) DO UPDATE home_id = excluded.home_id
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to set player's home shell: {}", player, e);
        }
    }
}
