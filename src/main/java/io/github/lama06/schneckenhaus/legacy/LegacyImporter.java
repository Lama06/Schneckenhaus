package io.github.lama06.schneckenhaus.legacy;

import io.github.lama06.schneckenhaus.shell.ShellCreationType;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.WoodType;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public final class LegacyImporter extends ConstantsHolder {
    private World world;

    public void loadLegacyDataIfNecessary() {
        world = Bukkit.getWorld("schneckenhaus");
        if (world == null) {
            return;
        }
        PersistentDataContainer worldPdc = world.getPersistentDataContainer();
        Integer nextId = worldPdc.get(new NamespacedKey(plugin, "next_id"), PersistentDataType.INTEGER);
        if (nextId == null) {
            return;
        }
        for (int i = 1; i <= 5; i++) {
            logger.warn("found legacy shell data! don't interrupt the import process! importing...".toUpperCase(Locale.ROOT));
        }

        for (int id = 1; id <= nextId - 1; id++) {
            int finalId = id;
            try {
                plugin.getDatabase().executeTransaction(() -> {
                    loadLegacyShell(finalId);
                    return null;
                });
            } catch (Exception e) {
                logger.error("failed to import shell {}", id, e);
            }
        }
        worldPdc.remove(new NamespacedKey(plugin, "next_id"));
    }

    private void loadLegacyShell(int id) throws SQLException {
        logger.warn("importing legacy shell with id {}", id);

        ShellPosition position = ShellPosition.id(world, id);
        PersistentDataContainer shellPdc = position.getCornerBlock().getChunk().getPersistentDataContainer();
        if (shellPdc.getOrDefault(new NamespacedKey(plugin, "deleted"), PersistentDataType.BOOLEAN, false)) {
            return;
        }
        String type = shellPdc.get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING);
        UUID creator = UUID.fromString(shellPdc.get(new NamespacedKey(plugin, "creator"), PersistentDataType.STRING));

        String insertSql = """
            INSERT INTO shells(id, world, position, creation_type, creator, type, enter_permission_mode, build_permission_mode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            int i = 1;
            statement.setInt(i++, id);
            statement.setString(i++, world.getName());
            statement.setInt(i++, id);
            statement.setString(i++, ShellCreationType.COMMAND.name().toLowerCase(Locale.ROOT));
            statement.setString(i++, creator.toString());
            statement.setString(i++, type);
            statement.setString(i++, ShellPermissionMode.EVERYBODY.name().toLowerCase(Locale.ROOT));
            statement.setString(i++, ShellPermissionMode.EVERYBODY.name().toLowerCase(Locale.ROOT));
            statement.executeUpdate();
        }

        String insertOwnerSql = """
            INSERT INTO shell_permissions(id, player, owner)
            VALUES (?, ?, TRUE)
            """;
        try (PreparedStatement statement = connection.prepareStatement(insertOwnerSql)) {
            statement.setInt(1, id);
            statement.setString(2, creator.toString());
            statement.executeUpdate();
        }

        if (type.equals("shulker") || type.equals("chest")) {
            int size = shellPdc.get(new NamespacedKey(plugin, "size"), PersistentDataType.INTEGER);
            String insertSizeSql = """
                INSERT INTO sized_shells(id, size)
                VALUES (?, ?)
                """;
            try (PreparedStatement statement = connection.prepareStatement(insertSizeSql)) {
                statement.setInt(1, id);
                statement.setInt(2, size);
                statement.executeUpdate();
            }
        }

        if (type.equals("shulker")) {
            DyeColor color = DyeColor.valueOf(shellPdc.get(new NamespacedKey(plugin, "color"), PersistentDataType.STRING));
            String insertShulkerSql = """
                INSERT INTO shulker_shells(id, color, rainbow)
                VALUES (?, ?, FALSE)
                """;
            try (PreparedStatement statement = connection.prepareStatement(insertShulkerSql)) {
                statement.setInt(1, id);
                statement.setString(2, color.name().toLowerCase(Locale.ROOT));
                statement.executeUpdate();
            }
        }

        if (type.equals("chest")) {
            String insertChestSql = """
                INSERT INTO chest_shells(id, wood)
                VALUES (?, ?)
                """;
            try (PreparedStatement statement = connection.prepareStatement(insertChestSql)) {
                statement.setInt(1, id);
                statement.setString(2, WoodType.OAK.name().toLowerCase(Locale.ROOT));
                statement.executeUpdate();
            }
        }

        if (type.equals("custom")) {
            String template = shellPdc.get(new NamespacedKey(plugin, "template"), PersistentDataType.STRING);
            String insertCustomSql = """
                INSERT INTO custom_shells(id, template)
                VALUES (?, ?)
                """;
            try (PreparedStatement statement = connection.prepareStatement(insertCustomSql)) {
                statement.setInt(1, id);
                statement.setString(2, template);
                statement.executeUpdate();
            }
        }
    }
}
