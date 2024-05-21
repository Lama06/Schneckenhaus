package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LeaveShellSystem implements Listener {
    public LeaveShellSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::detectPlayerLeaveSnailShellUnexpectedly, 0, 1);
    }

    @EventHandler
    private void teleportBack(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (!clickedBlock.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        if (!Tag.DOORS.isTagged(clickedBlock.getType())) {
            return;
        }
        final GridPosition position = CoordinatesGridPosition.fromWorldPosition(clickedBlock.getLocation());
        if (position == null) {
            return;
        }
        final Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(position);
        if (shell == null) {
            return;
        }
        if (!shell.getBlocks().containsKey(event.getClickedBlock())) { // don't handle doors placed by the player
            return;
        }
        event.setCancelled(true);
        schneckenPlayer.teleportBack();
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
    }

    /**
     * Detect when a player leaves a snail shell without clicking on the door,
     * e.g. because he or she dies, executes a command etc.
     * This is necessary to clear the previous location history.
     * Otherwise, the player can't enter a snail shell again which is stored as a previous location.
     */
    private void detectPlayerLeaveSnailShellUnexpectedly() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
                continue;
            }
            new SchneckenPlayer(player); // Upgrade data
            SchneckenPlayer.PREVIOUS_LOCATIONS.remove(player);
        }
    }
}
