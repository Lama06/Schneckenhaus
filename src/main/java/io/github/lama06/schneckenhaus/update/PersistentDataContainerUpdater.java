package io.github.lama06.schneckenhaus.update;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.persistence.PersistentDataContainer;

public abstract class PersistentDataContainerUpdater extends Updater<PersistentDataContainer> {
    public static final Attribute<PluginVersion> DATA_VERSION = new Attribute<>("data_version", PluginVersion.DATA_TYPE);

    protected abstract PluginVersion getDefaultVersion();

    @Override
    protected final void setDataVersion(final PluginVersion version) {
        final PersistentDataContainer data = getData();
        DATA_VERSION.set(data, version);
    }

    @Override
    protected final PluginVersion getDataVersion() {
        final PersistentDataContainer data = getData();
        return DATA_VERSION.getOrDefault(data, getDefaultVersion());
    }
}
