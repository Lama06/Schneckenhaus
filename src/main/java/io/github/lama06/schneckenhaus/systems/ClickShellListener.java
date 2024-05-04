package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ClickShellListener implements Listener {
    @EventHandler
    private void teleportToShell(final PlayerInteractEvent event) {
        final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final BlockState state = clickedBlock.getState();
        if (!(state instanceof final TileState tileState)) {
            return;
        }
        final Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        event.setCancelled(true);
        final Player player = event.getPlayer();
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        if (!player.hasPermission(Permissions.ENTER)) {
            return;
        }
        if (player.getWorld().equals(plugin.getWorld().getBukkit()) && !plugin.getSchneckenConfig().nesting) {
            return;
        }
        final IdGridPosition position = new IdGridPosition(id);
        final Shell<?> shell = plugin.getWorld().getShell(position);
        if (shell == null) {
            return;
        }
        if (schneckenPlayer.isInside(position)) {
            return;
        }
        schneckenPlayer.pushPreviousLocation(player.getLocation());
        player.teleport(shell.getPosition().getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 1);
    }
}
