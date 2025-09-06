package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public final class LeaveShellSystem extends System {
    public LeaveShellSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenhausPlugin.INSTANCE, this::detectPlayerLeaveSnailShellUnexpectedly, 0, 40);
    }

    @EventHandler
    private void teleportBackUsingDoor(PlayerInteractEvent event) {
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
        if (!shell.isExitBlock(block)) {
            return;
        }

        event.setCancelled(true);
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(event.getPlayer());
        schneckenhausPlayer.leave();
    }

    @EventHandler
    private void teleportBackQuickly(PlayerSwapHandItemsEvent event) {
        if (!Permission.QUICKLY_ENTER_SHELL.check(event.getPlayer())) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(event.getMainHandItem());
        if (shell == null) {
            return;
        }
        if (event.getPlayer().isSneaking()) {
            return;
        }
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(event.getPlayer());
        schneckenhausPlayer.leave();
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
