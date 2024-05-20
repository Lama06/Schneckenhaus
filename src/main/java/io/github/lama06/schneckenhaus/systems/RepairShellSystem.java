package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public final class RepairShellSystem implements Listener {
    private final int DELAY = 5;

    public RepairShellSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::repairShells, DELAY, DELAY);
    }

    private void repairShells() {
        final SchneckenWorld world = SchneckenPlugin.INSTANCE.getWorld();
        for (final Player player : world.getBukkit().getPlayers()) {
            final GridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
            if (position == null) {
                continue;
            }
            final Shell<?> shell = world.getShell(position);
            if (shell == null) {
                continue;
            }
            shell.place();
        }
    }
}
