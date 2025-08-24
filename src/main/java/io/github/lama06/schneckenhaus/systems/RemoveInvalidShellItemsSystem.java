package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RemoveInvalidShellItemsSystem extends System {
    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20, 20);
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) {
                    continue;
                }
                Integer id = item.getPersistentDataContainer().get(new NamespacedKey(plugin, Shell.ITEM_ID_ATTRIBUTE), PersistentDataType.INTEGER);
                if (id == null) {
                    continue;
                }

                String sql = """
                    SELECT 1
                    FROM shells
                    WHERE id = ?
                    """;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, id);
                    ResultSet result = statement.executeQuery();
                    if (result.next()) {
                        continue;
                    }
                } catch (SQLException e) {
                    logger.error("failed to query if shell with id {} still exists", id, e);
                    continue;
                }

                inventory.setItem(slot, null);
                logger.info("removed invalid shell item from slot {} of {}'s inventory", slot, player.getName());
            }
        }
    }
}
