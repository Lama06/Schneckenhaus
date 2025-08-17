package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.HopperInventorySearchEvent;

public final class HopperSystem extends System {
    @Override
    public boolean isEnabled() {
        return config.getHoppers().isEnabled();
    }

    @EventHandler
    private void transferItems(HopperInventorySearchEvent event) {
        Shell shell = plugin.getShellManager().getLinkedShell(event.getSearchBlock());
        if (shell == null) {
            return;
        }
        if (!config.getHoppers().check(shell)) {
            event.setInventory(null);
            return;
        }
        BlockArea area = shell.getArea();
        int layer = switch (event.getContainerType()) {
            case SOURCE -> 1;
            case DESTINATION -> area.getHeight() - 2;
        };
        for (BlockPosition hopperPosition : area.getLayer(layer)) {
            Block hopper = hopperPosition.getBlock(shell.getWorld());
            if (!(hopper.getBlockData() instanceof org.bukkit.block.data.type.Hopper hopperData)) {
                continue;
            }
            if (!hopperData.isEnabled()) {
                continue;
            }
            if (!(hopper.getState() instanceof Hopper hopperState)) {
                continue;
            }
            event.setInventory(hopperState.getInventory());
            return;
        }
    }
}
