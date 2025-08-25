package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class DispenserSystem extends System {
    @EventHandler
    private void registerPlacementThroughDispenser(BlockDispenseEvent event) {
        ItemStack dispensedItem = event.getItem();
        Shell shell = plugin.getShellManager().getShell(dispensedItem);
        if (shell == null) {
            return;
        }
        if (!(event.getBlock().getBlockData() instanceof Dispenser dispenserData)) {
            return;
        }
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Dispenser dispenserState)) {
            return;
        }
        event.setCancelled(true);

        Bukkit.getScheduler().runTask(plugin, () -> {
            boolean removedFromDispenser = false;
            Inventory dispenserInventory = dispenserState.getInventory();
            for (int slot = 0; slot < dispenserInventory.getSize(); slot++) {
                ItemStack itemInDispenser = dispenserInventory.getItem(slot);
                if (itemInDispenser == null) {
                    continue;
                }
                if (!shell.equals(plugin.getShellManager().getShell(itemInDispenser))) {
                    continue;
                }
                dispenserInventory.setItem(slot, null);
                removedFromDispenser = true;
                break;
            }
            if (!removedFromDispenser) {
                return;
            }

            Block shellPlacement = event.getBlock().getRelative(dispenserData.getFacing());
            shellPlacement.setType(shell.getPlacementBlockType());
            BlockState state = shellPlacement.getState();
            shell.initializePlacementBlockState(state);
            state.update();
            plugin.getShellManager().registerPlacedShell(shell, shellPlacement, null);
        });
    }
}
