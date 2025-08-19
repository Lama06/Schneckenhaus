package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public final class ProtectShellSystem extends System {
    @EventHandler
    private void preventBlockBreaking(BlockBreakEvent event) {
        Block block = event.getBlock();
        ShellPosition position = ShellPosition.block(event.getBlock());
        if (position == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(position);
        if (shell == null) {
            return;
        }
        Map<Block, BlockData> blocks = shell.getBlocks();
        if (!blocks.containsKey(block)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventPistonMoveShell(BlockPistonExtendEvent event) {
        preventPistonMoveShell(event, event.getBlocks());
    }

    @EventHandler
    private void preventPistonMoveShell(BlockPistonRetractEvent event) {
        preventPistonMoveShell(event, event.getBlocks());
    }

    private void preventPistonMoveShell(BlockPistonEvent event, List<Block> movedBlocks) {
        Block piston = event.getBlock();
        ShellPosition position = ShellPosition.block(piston);
        if (position == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(position);
        if (shell == null) {
            return;
        }
        Map<Block, BlockData> shellBlocks = shell.getBlocks();
        for (Block movedBlock : movedBlocks) {
            if (shellBlocks.containsKey(movedBlock)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void preventExplosion(EntityExplodeEvent event) {
        preventExplosion(event.blockList());
    }

    @EventHandler
    private void preventExplosion(BlockExplodeEvent event) {
        preventExplosion(event.blockList());
    }

    private void preventExplosion(List<Block> destroyedBlocks) {
        Map<ShellPosition, Set<Block>> shellBlocks = new HashMap<>();
        Iterator<Block> iterator = destroyedBlocks.iterator();
        while (iterator.hasNext()) {
            Block destroyedBlock = iterator.next();
            ShellPosition position = ShellPosition.block(destroyedBlock);
            if (position == null) {
                continue;
            }
            if (!shellBlocks.containsKey(position)) {
                Shell shell = plugin.getShellManager().getShell(position);
                if (shell == null) {
                    continue;
                }
                shellBlocks.put(position, shell.getBlocks().keySet());
            }
            Set<Block> blocks = shellBlocks.get(position);
            if (!blocks.contains(destroyedBlock)) {
                continue;
            }
            iterator.remove();
        }
    }
}
