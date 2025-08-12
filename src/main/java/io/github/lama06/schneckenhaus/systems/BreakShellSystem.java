package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;

public final class BreakShellSystem extends System {
    @EventHandler(priority = EventPriority.LOW) // call before #dropShellForPlayerInCreativeMode
    private void preventTheft(BlockBreakEvent event) {
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        if (!config.getTheftPrevention().check(shell)) {
            return;
        }
        if (Permission.BYPASS_THEFT_PREVENTION.check(event.getPlayer())) {
            return;
        }
        if (shell.getOwners().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(Message.ERROR_STEAL_PERMISSION.toComponent(NamedTextColor.RED));
    }

    @EventHandler(ignoreCancelled = true)
    private void dropShellForPlayerInCreativeMode(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            return;
        }
        Block block = event.getBlock();
        Shell shell = plugin.getShellManager().getLinkedShell(block);
        if (shell == null) {
            return;
        }

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), shell.createItem());
    }

    @EventHandler(ignoreCancelled = true)
    private void unregisterPlacedShellWhenBroken(BlockBreakEvent event) {
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        plugin.getShellManager().unregisterPlacedShell(event.getBlock());
    }

    @EventHandler
    private void preserveShellIdWhenBroken(BlockDropItemEvent event) {
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        event.getItems().clear();
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().toCenterLocation(), shell.createItem());
    }
}
