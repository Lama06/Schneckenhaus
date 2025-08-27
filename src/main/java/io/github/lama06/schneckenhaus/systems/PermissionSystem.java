package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PermissionSystem extends System {
    private static final int ENFORCE_ENTER_PERMISSIONS_DELAY = 40;

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(
            SchneckenhausPlugin.INSTANCE,
            this::enforceEnterPermissions,
            ENFORCE_ENTER_PERMISSIONS_DELAY,
            ENFORCE_ENTER_PERMISSIONS_DELAY
        );
    }

    private void enforceEnterPermissions() {
        for (Player player : plugin.getShellManager().getPlayersInShellWorlds()) {
            Shell shell = plugin.getShellManager().getShellAt(player);
            if (shell == null) {
                continue;
            }
            if (!shell.getEnterPermission().hasPermission(player)) {
                new SchneckenhausPlayer(player).leave();
            }
        }
    }

    @EventHandler
    private void preventUnallowedBuilding(BlockPlaceEvent event) {
        Shell shell = plugin.getShellManager().getShellAt(event.getBlock());
        if (shell == null) {
            return;
        }
        if (!shell.getBuildPermission().hasPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void preventUnallowedBuilding(BlockBreakEvent event) {
        Shell shell = plugin.getShellManager().getShellAt(event.getBlock());
        if (shell == null) {
            return;
        }
        if (!shell.getBuildPermission().hasPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void lockShellDoor(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
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

        Player player = event.getPlayer();
        if (!Permission.CHANGE_ENTER_PERMISSION.require(player)) {
            return;
        }
        if (!shell.getOwners().contains(player)) {
            return;
        }
        ShellPermissionMode mode = shell.getEnterPermission().getMode();
        ShellPermissionMode newMode = mode == ShellPermissionMode.EVERYBODY ? ShellPermissionMode.NOBODY : ShellPermissionMode.EVERYBODY;
        shell.getEnterPermission().setMode(newMode);
        if (newMode == ShellPermissionMode.EVERYBODY) {
            player.sendMessage(Message.UNLOCK_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Message.LOCK_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN));
        }
    }
}
