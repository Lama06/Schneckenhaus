package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public final class CustomShellImporter extends ConstantsHolder implements AutoCloseable {
    private final String fileName;
    private final String name;
    private final World templateWorld;
    private final BlockPosition templateCorner;

    private Connection connection;
    private final CustomShellConfig config = new CustomShellConfig();

    public CustomShellImporter(String fileName, String name, World templateWorld, BlockPosition templateCorner) {
        this.fileName = fileName;
        this.name = name;
        this.templateWorld = templateWorld;
        this.templateCorner = templateCorner;
    }

    public boolean importShell() {
        try {
            Path path = plugin.getDataPath().resolve(CustomShell.IMPORT_DIRECTORY).resolve(fileName);
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            importMetadata();
            importExitBlocks();
            importCraftingIngredients();
            importInitialBlocks();
            importAlternativeBlocks();
            placeTemplate();
            super.config.getCustom().put(name, config);
            plugin.getConfigManager().save();
            return true;
        } catch (Exception e) {
            logger.error("failed to import custom shell type", e);
            return false;
        }
    }

    private void placeTemplate() throws SQLException {
        String sql = """
            SELECT x, y, z, block FROM blocks
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int x = result.getInt(1);
                int y = result.getInt(2);
                int z = result.getInt(3);

                String dataString = result.getString(4);
                BlockData data;
                try {
                    data = Bukkit.createBlockData(dataString);
                } catch (IllegalArgumentException e) {
                    logger.error("failed to parse custom shell block data during import: {}", dataString, e);
                    continue;
                }

                templateCorner.add(x, y, z).getBlock(templateWorld).setBlockData(data);
            }
        }
    }

    private void importMetadata() throws SQLException {
        String sql = """
            SELECT item,
                   size_x, size_y, size_z,
                   menu_block_x, menu_block_y, menu_block_z,
                   spawn_x, spawn_y, spawn_z, spawn_yaw, spawn_pitch
            FROM custom_shell_type
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            result.next();

            NamespacedKey itemKey = NamespacedKey.fromString(result.getString("item"));
            if (itemKey != null) {
                Material item = Registry.MATERIAL.get(itemKey);
                if (item != null) {
                    config.setItem(item);
                } else {
                    logger.error("unknown item key during custom shell import: {}", itemKey);
                }
            } else {
                logger.error("invalid item key during custom shell import: {}", result.getString(1));
            }

            config.setTemplateWorld(templateWorld.getName());
            config.setTemplatePosition(new BlockArea(
                templateCorner,
                templateCorner.add(result.getInt("size_x") - 1, result.getInt("size_y") - 1, result.getInt("size_z") - 1)
            ));

            config.setMenuBlock(templateCorner.add(
                result.getInt("menu_block_x"),
                result.getInt("menu_block_y"),
                result.getInt("menu_block_z")
            ));

            config.setSpawnPosition(
                new Location(
                    null,
                    result.getDouble("spawn_x"), result.getDouble("spawn_y"), result.getDouble("spawn_z"),
                    result.getFloat("spawn_yaw"), result.getFloat("spawn_pitch")
                ).add(templateCorner.toVector())
            );
        }
    }

    private void importInitialBlocks() throws SQLException {
        String sql = """
            SELECT x, y, z
            FROM blocks
            WHERE initial
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int x = result.getInt(1);
                int y = result.getInt(2);
                int z = result.getInt(3);
                config.getInitialBlocks().add(templateCorner.add(x, y, z));
            }
        }
    }

    private void importAlternativeBlocks() throws SQLException {
        String sql = """
            SELECT x, y, z, alternative_block
            FROM alternative_blocks
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int x = result.getInt(1);
                int y = result.getInt(2);
                int z = result.getInt(3);
                BlockPosition block = templateCorner.add(x, y, z);
                Set<Material> alternatives = config.getAlternativeBlocks().computeIfAbsent(block, key -> new HashSet<>());
                NamespacedKey alternativeKey = NamespacedKey.fromString(result.getString(4));
                if (alternativeKey == null) {
                    logger.error("invalid block key during custom shell import: {}", result.getString(4));
                    continue;
                }
                Material alternative = Registry.MATERIAL.get(alternativeKey);
                if (alternative == null) {
                    logger.error("unknown block key during custom shell import: {}", alternativeKey);
                    continue;
                }
                alternatives.add(alternative);
            }
        }
    }

    private void importExitBlocks() throws SQLException {
        String sql = """
            SELECT x, y, z
            FROM exit_blocks
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                config.getExitBlocks().add(templateCorner.add(result.getInt(1), result.getInt(2), result.getInt(3)));
            }
        }
    }

    private void importCraftingIngredients() throws SQLException {
        String sql = """
            SELECT item, amount
            FROM crafting_ingredients
            """;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                NamespacedKey itemKey = NamespacedKey.fromString(result.getString(1));
                if (itemKey == null) {
                    logger.error("invalid crafting ingredient item key during custom shell import: {}", result.getString(1));
                    continue;
                }
                Material item = Registry.MATERIAL.get(itemKey);
                if (item == null) {
                    logger.error("unknown crafting ingredient item key during custom shell import: {}", itemKey);
                    continue;
                }
                config.getIngredients().add(new ItemConfig(item, result.getInt(2), null));
            }
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("failed to close sql database during import", e);
            }
        }
    }
}
