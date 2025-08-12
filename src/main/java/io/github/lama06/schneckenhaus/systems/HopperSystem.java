package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.HopperInventorySearchEvent;

public final class HopperSystem extends System {
    @EventHandler
    private void transferItemIntoShell(HopperInventorySearchEvent event) {
        if (event.getContainerType() != HopperInventorySearchEvent.ContainerType.DESTINATION) {
            return;
        }
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        if (!config.getHoppers().check(shell)) {
            return;
        }
        BlockArea area = shell.getArea();
        for (BlockPosition blockPosition : area.getLayer(area.getHeight() - 2)) {
            Block potentialContainer = blockPosition.getBlock(shell.getWorld());
            if (!(potentialContainer.getState() instanceof Container container)) {
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
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        if (!config.getHoppers().check(shell)) {
            return;
        }
        BlockArea area = shell.getArea();
        for (BlockPosition blockPosition : area.getLayer(1)) {
            Block potentialContainer = blockPosition.getBlock(shell.getWorld());
            if (!(potentialContainer.getState() instanceof Container container)) {
                continue;
            }
            event.setInventory(container.getInventory());
            return;
        }
    }
}
