package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class RestrictEnderChestSystem extends System {
    @EventHandler
    private void preventShellInEnderChest(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getType() != InventoryType.ENDER_CHEST) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (Permission.SHELL_IN_ENDER_CHEST.check(player)) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            Inventory enderChest = player.getEnderChest();
            for (int slot = 0; slot < enderChest.getSize(); slot++) {
                Shell shell = plugin.getShellManager().getShell(enderChest.getItem(slot));
                if (shell == null) {
                    continue;
                }
                enderChest.setItem(slot, null);
                player.give(shell.createItem());
            }
        });
    }
}
