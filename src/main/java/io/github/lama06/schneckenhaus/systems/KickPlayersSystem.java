package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public final class KickPlayersSystem implements Listener {
    private final int DELAY = 5;

    public KickPlayersSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::kickPlayers, DELAY, DELAY);
    }

    private void kickPlayers() {
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
            if (shell.isAllowedToEnter(player)) {
                continue;
            }
            SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
            schneckenPlayer.teleportBack();
        }
    }
}
