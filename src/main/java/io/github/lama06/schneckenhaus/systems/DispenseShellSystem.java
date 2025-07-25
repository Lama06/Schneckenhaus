package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class DispenseShellSystem implements Listener {
    @EventHandler
    private void on(BlockDispenseEvent event) {
        ItemStack dispensedItem = event.getItem();
        Integer shellId = Shell.ITEM_ID.get(dispensedItem.getItemMeta());
        if (shellId == null) {
            return;
        }
        Block dispenser = event.getBlock();
        if (!(dispenser.getBlockData() instanceof Dispenser dispenserData)) {
            return;
        }
        Block newBlock = dispenser.getRelative(dispenserData.getFacing());
        Bukkit.getScheduler().runTask(SchneckenPlugin.INSTANCE, () -> {
            if (!(newBlock.getState() instanceof TileState tileState)) {
                return;
            }
            Shell.BLOCK_ID.set(tileState, shellId);
            tileState.update();
        });
    }
}
