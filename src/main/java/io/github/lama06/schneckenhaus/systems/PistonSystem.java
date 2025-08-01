package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public final class PistonSystem implements Listener {
    @EventHandler
    private void on(BlockPistonExtendEvent event) {
        for (Block shellBlock : event.getBlocks()) {
            BlockState state = shellBlock.getState();
            if (!(state instanceof TileState tileState)) {
                continue;
            }
            Integer id = Shell.BLOCK_ID.get(tileState);
            if (id == null) {
                continue;
            }
            Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
            if (shell == null) {
                continue;
            }
            if (SchneckenPlugin.INSTANCE.getSchneckenConfig().theftPrevention) {
                event.setCancelled(true);
                return;
            }
            Listener[] listener = new Listener[1];
            listener[0] = new Listener() {
                @EventHandler
                public void on(ItemSpawnEvent itemSpawnEvent) {
                    if (shellBlock.getLocation().toCenterLocation().distance(itemSpawnEvent.getLocation().toCenterLocation()) < 0.1) {
                        itemSpawnEvent.setCancelled(true);
                        HandlerList.unregisterAll(listener[0]);
                        itemSpawnEvent.getEntity().getWorld().dropItem(itemSpawnEvent.getLocation(), shell.createItem());
                    }
                }
            };
            Bukkit.getPluginManager().registerEvents(listener[0], SchneckenPlugin.INSTANCE);
            Bukkit.getScheduler().runTaskLater(SchneckenPlugin.INSTANCE, () -> {
                HandlerList.unregisterAll(listener[0]);
            }, 3);
        }
    }
}
