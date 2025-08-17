package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

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
                Integer id = item.getPersistentDataContainer().get(new NamespacedKey(plugin, Shell.ITEM_ID), PersistentDataType.INTEGER);
                if (id == null) {
                    continue;
                }
                Shell shell = plugin.getShellManager().getShell(id);
                if (shell != null) {
                    continue;
                }
                inventory.setItem(slot, null);
                logger.info("removed invalid shell item from slot {} of {}'s inventory", slot, player.getName());
            }
        }
    }
}
