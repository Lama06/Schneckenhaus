package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public final class ProtectShellSystem extends System {
    @EventHandler(priority = EventPriority.LOW)
    private void preventBlockBreaking(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        ShellPosition shellPosition = ShellPosition.block(event.getBlock());
        if (shellPosition == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(shellPosition);
        if (shell == null) {
            return;
        }
        Map<Block, BlockData> shellBlocks = shell.getBlocks();
        if (shell.isBlockTypeAllowed(shellBlocks, brokenBlock, Material.AIR)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW) // call before PlaceShellSystem
    private void preventBlockPlacement(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        ShellPosition shellPosition = ShellPosition.block(event.getBlock());
        if (shellPosition == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(shellPosition);
        if (shell == null) {
            return;
        }
        Map<Block, BlockData> shellBlocks = shell.getBlocks();
        if (shell.isBlockTypeAllowed(shellBlocks, placedBlock, placedBlock.getType())) {
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
        Map<ShellPosition, Map<Block, BlockData>> shellBlocks = new HashMap<>();
        Iterator<Block> iterator = destroyedBlocks.iterator();
        while (iterator.hasNext()) {
            Block destroyedBlock = iterator.next();
            ShellPosition position = ShellPosition.block(destroyedBlock);
            if (position == null) {
                continue;
            }
            Shell shell = plugin.getShellManager().getShell(position);
            if (shell == null) {
                continue;
            }
            if (!shellBlocks.containsKey(position)) {
                shellBlocks.put(position, shell.getBlocks());
            }
            Map<Block, BlockData> blocks = shellBlocks.get(position);
            if (shell.isBlockTypeAllowed(blocks, destroyedBlock, Material.AIR)) {
                continue;
            }
            iterator.remove();
        }
    }
}
