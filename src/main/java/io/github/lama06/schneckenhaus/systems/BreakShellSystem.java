package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public final class BreakShellSystem implements Listener {
    @EventHandler
    private void preserveShellIdWhenBroken(final BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof final TileState tileState)) {
            return;
        }
        final Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        if (event.getItems().size() != 1) {
            return;
        }
        final ItemStack item = event.getItems().get(0).getItemStack();
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        Shell.ITEM_ID.set(itemData, id);
        item.setItemMeta(meta);
    }
}
