package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public final class ShellManager extends ConstantsHolder {
    public boolean isShellWorld(World world) {
        return config.getWorlds().containsKey(world.getName());
    }

    public Set<World> getShellWorlds() {
        return config.getWorlds().keySet().stream()
            .map(Bukkit::getWorld)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public Set<Player> getPlayersInShellWorlds() {
        return getShellWorlds().stream()
            .map(World::getPlayers)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    public Set<Shell> getInhabitedShells() {
        return getPlayersInShellWorlds().stream()
            .map(Entity::getLocation)
            .map(ShellPosition::location)
            .filter(Objects::nonNull)
            .map(this::getShell)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public Shell getShell(int id) {
        String type;
        String queryTypeSql = """
            SELECT type
            FROM shells
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(queryTypeSql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            type = result.getString("type");
        } catch (SQLException e) {
            logger.error("failed to query shell type: {}", id, e);
            return null;
        }

        ShellFactory factory = ShellFactories.getByName(type);
        if (factory == null) {
            return null;
        }
        return factory.loadShell(id);
    }

    public Shell getShell(ShellPosition position) {
        if (position == null) {
            return null;
        }

        int id;
        String sql = """
            SELECT id
            FROM shells
            WHERE world = ? AND position = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, position.getWorld().getName());
            statement.setInt(2, position.getId());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            id = result.getInt("id");
        } catch (SQLException e) {
            logger.error("failed to query shell id: {}", position, e);
            return null;
        }

        return getShell(id);
    }

    public Shell getShell(Location location) {
        ShellPosition position = ShellPosition.location(location);
        if (position == null) {
            return null;
        }
        return getShell(position);
    }

    public Shell getShellAt(Entity entity) {
        return getShell(entity.getLocation());
    }

    public Integer getShellId(ItemStack item) {
        if (item == null) {
            return null;
        }
        PersistentDataContainerView data = item.getPersistentDataContainer();
        return data.get(new NamespacedKey(plugin, Shell.ITEM_ID_ATTRIBUTE), PersistentDataType.INTEGER);
    }

    public Shell getShell(ItemStack item) {
        Integer id = getShellId(item);
        if (id == null) {
            return null;
        }
        return getShell(id);
    }

    private Integer getLinkedShellIdLegacy(Block block) {
        if (!(block.getState() instanceof TileState state)) {
            return null;
        }
        PersistentDataContainer data = state.getPersistentDataContainer();
        Integer id = data.get(new NamespacedKey(plugin, "id"), PersistentDataType.INTEGER);
        if (id == null) {
            return null;
        }
        Shell shell = getShell(id);
        if (shell == null) {
            return id;
        }
        String sql = """
            INSERT INTO shell_placements(world, x, y, z, id)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getX());
            statement.setInt(3, block.getY());
            statement.setInt(4, block.getZ());
            statement.setInt(5, shell.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to insert legacy shell placement");
            return id;
        }
        data.remove(new NamespacedKey(plugin, "id"));
        return id;
    }

    public Integer getLinkedShellId(Block block) {
        Integer id = getLinkedShellIdLegacy(block);
        if (id != null) {
            return id;
        }

        String sql = """
            SELECT id
            FROM shell_placements
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getX());
            statement.setInt(3, block.getY());
            statement.setInt(4, block.getZ());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return null;
            }
            return result.getInt(1);
        } catch (SQLException e) {
            logger.error("failed to query shell id: {}", block, e);
            return null;
        }
    }

    public Shell getLinkedShell(Block block) {
        Integer id = getLinkedShellId(block);
        if (id == null) {
            return null;
        }
        return getShell(id);
    }

    public Shell getShellAt(Block block) {
        return getShell(ShellPosition.block(block));
    }

    public Set<Integer> getShellIds(String world, int chunkX, int chunkZ) {
        Set<Integer> ids = new HashSet<>();
        String sql = """
            SELECT id
            FROM shell_placements
            WHERE world = ?
            AND x BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            statement.setInt(2, chunkX * 16);
            statement.setInt(3, chunkX * 16 + 15);
            statement.setInt(4, chunkZ * 16);
            statement.setInt(5, chunkZ * 16 + 15);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ids.add(result.getInt("id"));
            }
            return ids;
        } catch (SQLException e) {
            logger.error("failed to query shell ids in chunk: {} {}", chunkX, chunkZ, e);
            return Set.of();
        }
    }

    public Set<Integer> getShellIds(Chunk chunk) {
        return getShellIds(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public Set<Shell> getShells(Chunk chunk) {
        return getShellIds(chunk).stream().map(this::getShell).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<ShellPlacement> getShellPlacements(Block center, int range) {
        Set<ShellPlacement> shells = new HashSet<>();
        String sql = """
            SELECT id, world, x, y, z
            FROM shell_placements
            WHERE world = ?
            AND x BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, center.getWorld().getName());
            statement.setInt(2, center.getX() - range);
            statement.setInt(3, center.getX() + range);
            statement.setInt(4, center.getZ() - range);
            statement.setInt(5, center.getZ() + range);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Shell shell = getShell(result.getInt(1));
                if (shell == null) {
                    continue;
                }
                World world = Bukkit.getWorld(result.getString(2));
                if (world == null) {
                    continue;
                }
                Block block = world.getBlockAt(result.getInt(3), result.getInt(4), result.getInt(5));
                shells.add(new ShellPlacement(shell, block));
            }
            return shells;
        } catch (SQLException e) {
            logger.error("failed to query nearby shells: {} {}", center, range, e);
            return Set.of();
        }
    }

    public Set<ShellPlacement> getShellPlacements(Shell shell) {
        Set<ShellPlacement> shells = new HashSet<>();
        String sql = """
            SELECT world, x, y, z
            FROM shell_placements
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shell.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                World world = Bukkit.getWorld(result.getString(1));
                if (world == null) {
                    continue;
                }
                Block block = world.getBlockAt(result.getInt(2), result.getInt(3), result.getInt(4));
                shells.add(new ShellPlacement(shell, block));
            }
            return shells;
        } catch (SQLException e) {
            logger.error("failed to query shell placements: {}", shell.getId(), e);
            return Set.of();
        }
    }

    public void registerPlacedShell(Shell shell, Block block, Player player) {
        String sql = """
            INSERT INTO shell_placements(world, x, y, z, id, placed_by)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (world, x, y, z)
            DO UPDATE SET id = excluded.id, placed_by = excluded.placed_by
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getX());
            statement.setInt(3, block.getY());
            statement.setInt(4, block.getZ());
            statement.setInt(5, shell.getId());
            statement.setString(6, player == null ? null : player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to insert shell placement: {}, {}", shell.getId(), block, e);
        }
    }

    public void unregisterPlacedShell(Block block) {
        String sql = """
            DELETE FROM shell_placements
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getX());
            statement.setInt(3, block.getY());
            statement.setInt(4, block.getZ());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("failed to delete shell placement from database: {}", block, e);
        }
    }

    public int getAllTimeShellCount() {
        String sql = """
            SELECT seq
            FROM sqlite_sequence
            WHERE name = 'shells'
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            if (!result.next()) {
                return 0;
            }
            return result.getInt(1);
        } catch (SQLException e) {
            logger.error("failed to get all time shell count", e);
            return 0;
        }
    }

    public int getCurrentShellCount() {
        String sql = """
            SELECT COUNT(*)
            FROM shells
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            if (!result.next()) {
                return 0;
            }
            return result.getInt(1);
        } catch (SQLException e) {
            logger.error("failed to get total shell count", e);
            return 0;
        }
    }

    public int getWorldShellCount(String world) {
        String sql = """
            SELECT COUNT(*)
            FROM shells
            WHERE world = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            ResultSet result = statement.executeQuery();
            return result.getInt(1);
        } catch (SQLException e) {
            logger.error("failed to get shell count for world {}", world, e);
            return 0;
        }
    }
}
