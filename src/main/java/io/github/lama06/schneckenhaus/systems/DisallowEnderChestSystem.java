package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DisallowEnderChestSystem implements Listener {
    @EventHandler
    private void on(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getType() != InventoryType.ENDER_CHEST) {
            return;
        }
        HumanEntity player = event.getWhoClicked();
        if (player.hasPermission("schneckenhaus.shells_in_enderchest")) {
            return;
        }
        Bukkit.getScheduler().runTask(SchneckenPlugin.INSTANCE, () -> {
            Inventory enderChest = player.getEnderChest();
            for (int slot = 0; slot < enderChest.getSize(); slot++) {
                ItemStack item = enderChest.getItem(slot);
                if (item == null) {
                    continue;
                }
                if (!Shell.ITEM_ID.has(item.getItemMeta())) {
                    continue;
                }
                enderChest.setItem(slot, null);
                ((Player) player).give(item);
            }
        });
    }
}
