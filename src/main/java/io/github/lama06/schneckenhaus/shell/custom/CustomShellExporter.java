package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Set;

public final class CustomShellExporter extends ConstantsHolder implements AutoCloseable {
    private final String name;
    private final CustomShellConfig config;
    private Connection connection;

    public CustomShellExporter(String name, CustomShellConfig config) {
        this.name = name;
        this.config = config;
    }

    public boolean export() {
        try {
            createFile();
            connection.setAutoCommit(false);
            createScheme();
            insertData();
            connection.commit();
            return true;
        } catch (Exception e) {
            logger.error("failed to export custom shell: {}", name, e);
            return false;
        }
    }

    private void createFile() throws IOException, SQLException {
        Path exportFolder = plugin.getDataPath().resolve(CustomShell.EXPORT_DIRECTORY);
        Path exportFile = exportFolder.resolve(name + CustomShell.FILE_EXTENSION);
        Files.deleteIfExists(exportFile);
        Files.createDirectories(exportFolder);
        connection = DriverManager.getConnection("jdbc:sqlite:%s".formatted(exportFile.toString()));
    }

    private void createScheme() throws SQLException, IOException {
        StringBuilder scheme = new StringBuilder();
        try (Reader schemeReader = new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("haus_scheme.sql")))) {
            int read;
            while ((read = schemeReader.read()) != -1) {
                scheme.append((char) read);
            }
        }

        try (Statement statement = connection.createStatement()) {
            for (String sql : scheme.toString().split(";")) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
        }
    }

    private void insertData() throws SQLException {
        insertDataVersion();
        insertMetadata();
        insertBlocks();
        insertExitBlocks();
        insertCraftingIngredients();
        insertBlockRestrictions();
    }

    private void insertDataVersion() throws SQLException {
        String sql = """
            INSERT INTO data_version(plugin_version, game_version)
            VALUES (?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, PluginVersion.current().toString());
            statement.setString(2, Bukkit.getMinecraftVersion());
            statement.executeUpdate();
        }
    }

    private void insertMetadata() throws SQLException {
        String sql = """
            INSERT INTO custom_shell_type(
                size_x, size_y, size_z,
                item,
                menu_block_x, menu_block_y, menu_block_z,
                spawn_x, spawn_y, spawn_z, spawn_yaw, spawn_pitch,
                protect_air
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;

            BlockArea area = config.getTemplatePosition();
            statement.setInt(i++, area.getWidthX());
            statement.setInt(i++, area.getHeight());
            statement.setInt(i++, area.getWidthZ());

            statement.setString(i++, config.getItem().getKey().toString());

            BlockPosition menuBlock = config.getMenuBlock();
            if (menuBlock != null) {
                menuBlock = menuBlock.subtract(area.getLowerCorner());
                statement.setInt(i, menuBlock.x());
                statement.setInt(i + 1, menuBlock.y());
                statement.setInt(i + 2, menuBlock.z());
            }
            i += 3;

            Location spawnPosition = config.getSpawnPosition();
            if (spawnPosition != null) {
                spawnPosition = spawnPosition.subtract(area.getLowerCorner().toVector());
                statement.setDouble(i, spawnPosition.getX());
                statement.setDouble(i + 1, spawnPosition.getY());
                statement.setDouble(i + 2, spawnPosition.getZ());
                statement.setFloat(i + 3, spawnPosition.getYaw());
                statement.setFloat(i + 4, spawnPosition.getPitch());
            }
            i += 5;

            statement.setBoolean(i++, config.isProtectAir());

            statement.executeUpdate();
        }
    }

    private void insertBlocks() throws SQLException {
        String sql = """
            INSERT INTO blocks(x, y, z, block)
            VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            BlockArea area = config.getTemplatePosition();
            for (BlockPosition absolutePosition : area) {
                Block block = absolutePosition.getBlock(Bukkit.getWorld(config.getTemplateWorld()));
                if (block.isEmpty()) {
                    continue;
                }
                BlockPosition relativePosition = absolutePosition.subtract(area.getLowerCorner());
                statement.setInt(1, relativePosition.x());
                statement.setInt(2, relativePosition.y());
                statement.setInt(3, relativePosition.z());
                statement.setString(4, block.getBlockData().getAsString());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertExitBlocks() throws SQLException {
        String sql = """
            INSERT INTO exit_blocks(x, y, z)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BlockPosition absolutePosition : config.getExitBlocks()) {
                BlockPosition relativePosition = absolutePosition.subtract(config.getTemplatePosition().getLowerCorner());
                statement.setInt(1, relativePosition.x());
                statement.setInt(2, relativePosition.y());
                statement.setInt(3, relativePosition.z());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertCraftingIngredients() throws SQLException {
        String sql = """
            INSERT INTO crafting_ingredients(item, amount)
            VALUES (?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ItemConfig ingredient : config.getIngredients()) {
                statement.setString(1, ingredient.getItem().getKey().toString());
                statement.setInt(2, ingredient.getAmount());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertBlockRestrictions() throws SQLException {
        String sql = """
            INSERT INTO block_restrictions(x, y, z, restriction)
            VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BlockPosition position : config.getBlockRestrictions().keySet()) {
                BlockPosition relativePosition = position.subtract(config.getTemplatePosition().getLowerCorner());
                statement.setInt(1, relativePosition.x());
                statement.setInt(2, relativePosition.y());
                statement.setInt(3, relativePosition.z());
                Set<Material> restrictions = config.getBlockRestrictions().get(position);
                if (restrictions.isEmpty()) {
                    statement.setString(4, null);
                    statement.addBatch();
                }
                for (Material restriction : restrictions) {
                    statement.setString(4, restriction.getKey().toString());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("failed to close sqlite database", e);
        }
    }
}
