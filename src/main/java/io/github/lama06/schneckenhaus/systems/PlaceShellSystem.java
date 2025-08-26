package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public final class PlaceShellSystem extends System {
    @EventHandler(ignoreCancelled = true)
    private void placeShell(BlockPlaceEvent event) {
        Shell shell = plugin.getShellManager().getShell(event.getItemInHand());
        if (shell == null) {
            return;
        }
        if (!Permission.PLACE_SHELL.require(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        plugin.getShellManager().registerPlacedShell(shell, block, event.getPlayer());
    }
}
