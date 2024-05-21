package io.github.lama06.schneckenhaus.player;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.LocationPersistentDataType;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SchneckenPlayer {
    public static final Attribute<List<Location>> PREVIOUS_LOCATIONS = new Attribute<>(
            "previous_locations",
            PersistentDataType.LIST.listTypeFrom(LocationPersistentDataType.INSTANCE)
    );

    private final Player player;

    public SchneckenPlayer(final Player player) {
        this.player = Objects.requireNonNull(player);
        final PlayerUpdater upgrade = new PlayerUpdater(player);
        upgrade.update();
    }

    public boolean isInside(final GridPosition position) {
        if (position.equals(CoordinatesGridPosition.fromWorldPosition(player.getLocation()))) {
            return true;
        }
        final List<Location> previousLocations = PREVIOUS_LOCATIONS.getOrDefault(player, List.of());
        return previousLocations.stream().map(CoordinatesGridPosition::fromWorldPosition).anyMatch(position::equals);
    }

    public void pushPreviousLocation(final Location location) {
        final List<Location> previousLocations = new ArrayList<>(PREVIOUS_LOCATIONS.getOrDefault(player, List.of()));
        previousLocations.add(location);
        PREVIOUS_LOCATIONS.set(player, previousLocations);
    }

    public Location popPreviousLocation() {
        final List<Location> previousLocations = new ArrayList<>(PREVIOUS_LOCATIONS.getOrDefault(player, List.of()));
        if (previousLocations.isEmpty()) {
            return null;
        }
        final Location lastPreviousLocation = previousLocations.get(previousLocations.size() - 1);
        previousLocations.remove(previousLocations.size() - 1);
        PREVIOUS_LOCATIONS.set(player, previousLocations);
        return lastPreviousLocation;
    }
}
