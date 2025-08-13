package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public final class BreakShellSystem extends System {
    @EventHandler
    private void breakShell(BlockBreakEvent event) {
        Shell shell = plugin.getShellManager().getLinkedShell(event.getBlock());
        if (shell == null) {
            return;
        }
        if (preventTheft(event, shell)) {
            return;
        }
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), shell.createItem());
        plugin.getShellManager().unregisterPlacedShell(event.getBlock());
    }

    private boolean preventTheft(BlockBreakEvent event, Shell shell) {
        if (!config.getTheftPrevention().check(shell)) {
            return false;
        }
        if (Permission.BYPASS_THEFT_PREVENTION.check(event.getPlayer())) {
            return false;
        }
        if (shell.getOwners().contains(event.getPlayer().getUniqueId())) {
            return false;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(Message.ERROR_STEAL_PERMISSION.asComponent(NamedTextColor.RED));
        return true;
    }
}
