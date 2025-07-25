package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.shell.AccessMode;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LockShellSystem implements Listener {
    @EventHandler
    private void onClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (!Tag.DOORS.isTagged(clickedBlock.getType())) {
            return;
        }
        final Player player = event.getPlayer();
        final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
        if (position == null) {
            return;
        }
        final Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(position);
        if (shell == null) {
            return;
        }
        if (!shell.getBlocks().containsKey(clickedBlock)) {
            return;
        }
        event.setCancelled(true);

        boolean isLocked = Shell.ACCESS_MODE.get(shell) != AccessMode.EVERYBODY;

        if (!shell.getCreator().equals(player)) {
            String text = "You can't " + (isLocked ? "unlock" : "lock") + " this snail shell because you don't own it";
            player.sendMessage(Component.text(text, NamedTextColor.RED));
            return;
        }

        Shell.ACCESS_MODE.set(shell, isLocked ? AccessMode.EVERYBODY : AccessMode.NOBODY);

        isLocked = Shell.ACCESS_MODE.get(shell) != AccessMode.EVERYBODY;
        Component msg = Component.text()
            .append(
                Component.text("You successfully " + (isLocked ? "locked" : "unlocked") + " this snail shell.\n")
                    .color(NamedTextColor.GREEN)
            )
            .append(
                Component.text(
                    isLocked ?
                        "Others except for you can no longer enter it." :
                        "Now everyone can enter it."
                )
            )
            .build();
        player.sendMessage(msg);
    }
}