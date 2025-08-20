package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public final class PistonSystem extends System {
    @EventHandler
    private void onPistonRetract(BlockPistonRetractEvent event) {
        onPistonMove(event.getBlocks());
    }

    @EventHandler
    private void onPistonExtent(BlockPistonExtendEvent event) {
        onPistonMove(event.getBlocks());
    }

    private void onPistonMove(List<Block> blocks) {
        for (Block block : blocks) {
            Shell shell = plugin.getShellManager().getLinkedShell(block);
            if (shell == null) {
                continue;
            }
            block.setType(Material.AIR);
            plugin.getShellManager().unregisterPlacedShell(block);
            block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), shell.createItem());
        }
    }
}
