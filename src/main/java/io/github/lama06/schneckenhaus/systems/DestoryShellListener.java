package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public final class DestoryShellListener implements Listener {
    @EventHandler
    private void preventBreakSnailShell(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final SchneckenWorld world = SchneckenPlugin.INSTANCE.getWorld();
        if (!player.getWorld().equals(world.getBukkit())) {
            return;
        }
        final GridPosition position = CoordinatesGridPosition.fromWorldPosition(event.getBlock().getLocation());
        if (position == null) {
            return;
        }
        final Shell<?> shell = world.getShell(position);
        if (shell == null) {
            return;
        }
        if (!shell.getBlocks().containsKey(event.getBlock())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventExplosion(final BlockExplodeEvent event) {
        if (!event.getBlock().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventExplosion(final EntityExplodeEvent event) {
        if (!event.getEntity().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventBlockIgnite(final BlockIgniteEvent event) {
        if (!event.getBlock().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        event.setCancelled(true);
    }
}
