package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.ui.ShellScreen;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ShellMenuSystem extends System {
    @EventHandler
    private void openMenu(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShellAt(block);
        if (shell == null) {
            return;
        }
        if (!shell.isMenuBlock(block)) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        Permission permission = shell.getOwners().contains(player) ? Permission.OPEN_OWN_SNAIL_SHELL_MENU : Permission.OPEN_OTHER_SNAIL_SHELL_MENUS;
        if (!permission.require(event.getPlayer())) {
            return;
        }
        new ShellScreen(shell, player).open();
    }
}
