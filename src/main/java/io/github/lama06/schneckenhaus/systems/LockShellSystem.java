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

import static io.github.lama06.schneckenhaus.language.Translator.t;

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
            String text = isLocked ? t("snail_shell_unlock_fail") : t("snail_shell_lock_fail");
            player.sendMessage(Component.text(text, NamedTextColor.RED));
            return;
        }

        Shell.ACCESS_MODE.set(shell, isLocked ? AccessMode.EVERYBODY : AccessMode.NOBODY);

        isLocked = Shell.ACCESS_MODE.get(shell) != AccessMode.EVERYBODY;
        Component msg = Component.text()
            .append(
                Component.text(isLocked ? t("snail_shell_lock_success") : t("snail_shell_unlock_success"))
                    .color(NamedTextColor.GREEN)
            )
            .appendNewline()
            .append(
                Component.text(
                    isLocked ?
                        t("snail_shell_lock_success_hint") :
                        t("snail_shell_unlock_success_hint")
                )
            )
            .build();
        player.sendMessage(msg);
    }
}