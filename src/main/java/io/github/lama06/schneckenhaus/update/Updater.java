package io.github.lama06.schneckenhaus.update;

import io.github.lama06.schneckenhaus.util.PluginVersion;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class Updater<T> {
    protected abstract T getData();

    protected void applyChanges() { }

    protected abstract PluginVersion getDataVersion();

    protected abstract void setDataVersion(final PluginVersion version);

    protected abstract Map<PluginVersion, Runnable> getUpdates();

    public final void update() {
        final SortedMap<PluginVersion, Runnable> updates = new TreeMap<>(getUpdates());
        final PluginVersion currentDataVersion = getDataVersion();
        for (final PluginVersion pluginVersion : updates.tailMap(currentDataVersion).keySet()) {
            if (pluginVersion.equals(currentDataVersion)) {
                continue;
            }
            updates.get(pluginVersion).run();
        }
        setDataVersion(PluginVersion.current());
        applyChanges();
    }
}
