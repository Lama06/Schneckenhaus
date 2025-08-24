package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class ShellPlacement extends ConstantsHolder {
    private final Block block;

    public ShellPlacement(Block block) {
        this.block = block;
    }

    public Location getExitPositionOrFallback() {
        Location exitPosition = getExitPosition();
        if (exitPosition != null) {
            return exitPosition;
        }

        for (BlockFace face : List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
            Block neighbour = block.getRelative(face);
            if (!neighbour.isEmpty() || !neighbour.getRelative(BlockFace.UP).isEmpty() || neighbour.getRelative(BlockFace.DOWN).isEmpty()) {
                continue;
            }
            return neighbour.getLocation().toCenterLocation().add(0, 1, 0).setDirection(new Vector(
                -face.getModX(),
                -1,
                -face.getModZ()
            ));
        }

        return block.getLocation().toCenterLocation().add(0, 1, 0);
    }

    public Location getExitPosition() {
        String sql = """
            SELECT exit_position_x, exit_position_y, exit_position_z, exit_position_yaw, exit_position_pitch
            FROM shell_placements
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setWhereClauseParameters(statement, 1);
            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                return null;
            }
            if (result.getObject(1) == null) {
                return null;
            }
            return new Location(
                block.getWorld(),
                result.getDouble(1), result.getDouble(2), result.getDouble(3),
                result.getFloat(4), result.getFloat(5)
            );

        } catch (SQLException e) {
            logger.error("failed to query shell placement exit position: {}", block, e);
            return null;
        }
    }

    public void setExitPosition(Location exitPosition) {
        String sql = """
            UPDATE shell_placements
            SET exit_position_x = ?, exit_position_y = ?, exit_position_z = ?, exit_position_yaw = ?, exit_position_pitch = ?
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, exitPosition.getX());
            statement.setDouble(2, exitPosition.getY());
            statement.setDouble(3, exitPosition.getZ());
            statement.setFloat(4, exitPosition.getYaw());
            statement.setFloat(5, exitPosition.getPitch());
            setWhereClauseParameters(statement, 6);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell placement exit position: {}", block, e);
        }
    }

    public String getName() {
        String sql = """
            SELECT name
            FROM shell_placements
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setWhereClauseParameters(statement, 1);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            return result.getString(1);
        } catch (SQLException e) {
            logger.error("failed to query name of shell placement: {}", block, e);
            return null;
        }
    }

    public void setName(String name) {
        String sql = """
            UPDATE shell_placements
            SET name = ?
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            setWhereClauseParameters(statement, 2);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to update shell placement name: {}", block, e);
        }
    }

    public Shell getShell() {
        String sql = """
            SELECT id
            FROM shell_placements
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setWhereClauseParameters(statement, 1);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            return plugin.getShellManager().getShell(result.getInt(1));
        } catch (SQLException e) {
            logger.error("failed to query shell liked to shell placement: {}", block, e);
            return null;
        }
    }

    private int setWhereClauseParameters(PreparedStatement statement, int i) throws SQLException {
        statement.setString(i, block.getWorld().getName());
        statement.setInt(i + 1, block.getX());
        statement.setInt(i + 2, block.getY());
        statement.setInt(i + 3, block.getZ());
        return i + 4;
    }

    public Block getBlock() {
        return block;
    }
}
