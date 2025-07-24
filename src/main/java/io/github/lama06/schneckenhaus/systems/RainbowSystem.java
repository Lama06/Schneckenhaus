package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellConfig;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public final class RainbowSystem implements Listener {
    public RainbowSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::rainbow, 0, 20L * SchneckenPlugin.INSTANCE.getSchneckenConfig().rainbowModeDelay);
    }

    private void rainbow() {
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
            if (!ShulkerShell.RAINBOW.getOrDefault(shell, false)) {
                continue;
            }
            ShulkerShellConfig.COLOR.set(shell, EnumUtil.getNext(ShulkerShellConfig.COLOR.get(shell)));
        }
    }
}
