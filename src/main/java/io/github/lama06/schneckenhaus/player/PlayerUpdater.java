package io.github.lama06.schneckenhaus.player;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.LocationPersistentDataType;
import io.github.lama06.schneckenhaus.update.PersistentDataContainerUpdater;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Map;

public final class PlayerUpdater extends PersistentDataContainerUpdater {
    private final Player player;

    public PlayerUpdater(final Player player) {
        this.player = player;
    }

    @Override
    protected PersistentDataContainer getData() {
        return player.getPersistentDataContainer();
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
        final Location legacyPreviousLocation = new Attribute<>("previous_location", LocationPersistentDataType.INSTANCE).get(player);
        if (legacyPreviousLocation != null) {
            SchneckenPlayer.PREVIOUS_LOCATIONS.set(player, List.of(legacyPreviousLocation));
        }
    }
}
