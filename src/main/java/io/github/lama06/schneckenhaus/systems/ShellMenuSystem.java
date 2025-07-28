package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.screen.ShellScreen;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class ShellMenuSystem implements Listener {
    @EventHandler
    private void openMenuDoorstep(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(clickedBlock.getLocation());
        if (position == null) {
            return;
        }
        if (!clickedBlock.equals(position.getCornerBlock().getRelative(1, 0, 1))) {
            return;
        }
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(position);
        if (shell == null) {
            return;
        }
        event.setCancelled(true);
        if (!event.getPlayer().equals(shell.getCreator())) {
            event.getPlayer().sendMessage(Component.text(t("snail_shell_menu_open_fail"), NamedTextColor.RED));
            return;
        }
        new ShellScreen(shell, event.getPlayer()).open();
    }
}
