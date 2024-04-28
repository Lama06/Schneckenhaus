package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;
import io.github.lama06.schneckenhaus.update.PersistentDataContainerUpdater;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

public final class ShellUpdater extends PersistentDataContainerUpdater {
    private final GridPosition position;

    public ShellUpdater(final GridPosition position) {
        this.position = position;
    }

    @Override
    protected PersistentDataContainer getData() {
        return SchneckenPlugin.INSTANCE.getWorld().getShellData(position);
    }

    @Override
    protected PluginVersion getDefaultVersion() {
        return new PluginVersion(1, 0, 1);
    }

    @Override
    protected Map<PluginVersion, Runnable> getUpdates() {
        return Map.ofEntries(
                Map.entry(new PluginVersion(1, 1, 0), this::updateTo1_1_0)
        );
    }

    private void updateTo1_1_0() {
        final PersistentDataContainer data = getData();
        Shell.TYPE.set(data, ShulkerShellFactory.INSTANCE.getName());
    }
}
