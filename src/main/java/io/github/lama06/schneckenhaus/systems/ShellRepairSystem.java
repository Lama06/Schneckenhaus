package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;

public final class ShellRepairSystem extends System {
    @Override
    public boolean isEnabled() {
        return config.getRepairSystem().isEnabled();
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(SchneckenhausPlugin.INSTANCE, this::repairShells, 0, config.getRepairSystem().getDelay());
    }

    private void repairShells() {
        for (Shell shell : plugin.getShellManager().getInhabitedShells()) {
            if (!config.getRepairSystem().check(shell)) {
                continue;
            }
            shell.place();
        }
    }
}
