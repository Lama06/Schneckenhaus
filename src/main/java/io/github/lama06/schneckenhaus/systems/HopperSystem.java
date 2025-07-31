package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.HopperInventorySearchEvent;

public class HopperSystem implements Listener {
    @EventHandler
    private void transferItemIntoShell(HopperInventorySearchEvent event) {
        if (event.getContainerType() != HopperInventorySearchEvent.ContainerType.DESTINATION) {
            return;
        }
        if (!(event.getSearchBlock().getState() instanceof TileState tileState)) {
            return;
        }
        Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            return;
        }
        if (!(shell instanceof BuiltinShell<?> builtinShell)) {
            return;
        }
        BlockArea topLayer = new BlockArea(
            shell.getPosition().getCornerBlock().getRelative(1, builtinShell.getSize(), 1),
            shell.getPosition().getCornerBlock().getRelative(builtinShell.getSize(), builtinShell.getSize(), builtinShell.getSize())
        );
        for (BlockPosition blockPosition : topLayer) {
            Block potentialHopper = blockPosition.getBlock(SchneckenPlugin.INSTANCE.getWorld().getBukkit());
            if (!(potentialHopper.getState() instanceof Container container)) {
                continue;
            }
            event.setInventory(container.getInventory());
            return;
        }
    }

    @EventHandler
    private void transferItemOutOfShell(HopperInventorySearchEvent event) {
        if (event.getContainerType() != HopperInventorySearchEvent.ContainerType.SOURCE) {
            return;
        }
        if (!(event.getSearchBlock().getState() instanceof TileState tileState)) {
            return;
        }
        Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            return;
        }
        if (!(shell instanceof BuiltinShell<?> builtinShell)) {
            return;
        }
        BlockArea bottomLayer = new BlockArea(
            shell.getPosition().getCornerBlock().getRelative(1, 1, 1),
            shell.getPosition().getCornerBlock().getRelative(builtinShell.getSize(), 1, builtinShell.getSize())
        );
        for (BlockPosition blockPosition : bottomLayer) {
            Block potentialHopper = blockPosition.getBlock(SchneckenPlugin.INSTANCE.getWorld().getBukkit());
            if (!(potentialHopper.getState() instanceof Container container)) {
                continue;
            }
            event.setInventory(container.getInventory());
            return;
        }
    }
}
