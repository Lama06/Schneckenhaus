package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public final class PlaceShellSystem implements Listener {
    @EventHandler
    private void preserveShellIdWhenPlaced(final BlockPlaceEvent event) {
        final ItemStack itemInHand = event.getItemInHand();
        final ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) {
            // An user reported an exception which was throws because the item meta was null
            return;
        }
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        final Integer id = Shell.ITEM_ID.get(itemData);
        if (id == null) {
            return;
        }
        if (!Permissions.require(event.getPlayer(), "schneckenhaus.place_shells")) {
            event.setCancelled(true);
            return;
        }
        final Block block = event.getBlock();
        if (!(block.getState() instanceof final TileState tileState)) {
            return;
        }
        Shell.BLOCK_ID.set(tileState, id);
        tileState.update();
    }

}
