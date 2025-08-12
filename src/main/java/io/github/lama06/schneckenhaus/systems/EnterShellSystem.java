package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.player.ShellTeleportOptions;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class EnterShellSystem extends System {
    @EventHandler
    private void enterShell(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (player.isSneaking() && event.isBlockInHand()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getLinkedShell(block);
        if (shell == null) {
            return;
        }

        event.setCancelled(true);

        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(player);
        ShellTeleportOptions options = new ShellTeleportOptions();
        options.setPlaySound(true);
        options.setStorePreviousPositionWhenNesting(true);
        schneckenhausPlayer.enter(shell, options);
    }
}
