package io.github.lama06.schneckenhaus.player;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.LocationPersistentDataType;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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

    public void pushPreviousLocation(final Location location, final boolean nesting) {
        final List<Location> previousLocations = new ArrayList<>(PREVIOUS_LOCATIONS.getOrDefault(player, List.of()));
        if (!nesting && !previousLocations.isEmpty()) {
            return;
        }
        previousLocations.add(location);
        PREVIOUS_LOCATIONS.set(player, previousLocations);
    }

    public void pushPreviousLocation(final Location location) {
        pushPreviousLocation(location, true);
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

    public void teleportBack() {
        Location newLocation = popPreviousLocation();
        if (newLocation == null) {
            World world = Bukkit.getWorld("world");
            // Some servers don't have this world
            if (world == null) {
                world = Bukkit.getWorlds().stream()
                        .filter(Predicate.not(SchneckenPlugin.INSTANCE.getWorld().getBukkit()::equals))
                        .findFirst().orElse(null);
            }
            if (world == null) {
                final String error = "You can't be teleported back because no world was found.";
                player.spigot().sendMessage(new ComponentBuilder(error).color(ChatColor.RED).build());
                return;
            }
            newLocation = world.getSpawnLocation();
        }
        player.teleport(newLocation);
    }
}
