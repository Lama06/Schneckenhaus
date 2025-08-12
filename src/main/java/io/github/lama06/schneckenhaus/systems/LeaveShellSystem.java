package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LeaveShellSystem extends System {
    public LeaveShellSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::detectPlayerLeaveSnailShellUnexpectedly, 0, 10);
    }

    @EventHandler
    private void teleportBack(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShellAt(block);
        if (shell == null) {
            return;
        }
        if (!shell.isDoorBlock(block)) {
            return;
        }

        event.setCancelled(true);
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(event.getPlayer());
        schneckenhausPlayer.leave();
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
    }

    /**
     * Detect when a player leaves a snail shell without clicking on the door,
     * e.g. because he or she dies, executes a command etc.
     * This is necessary to clear the previous location history.
     * Otherwise, the player can't enter a snail shell again which is stored as a previous location.
     */
    private void detectPlayerLeaveSnailShellUnexpectedly() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (config.getWorlds().containsKey(player.getWorld().getName())) {
                return;
            }
            new SchneckenhausPlayer(player).clearPreviousLocations();
        }
    }
}
