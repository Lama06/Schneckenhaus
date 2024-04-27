package io.github.lama06.schneckenhaus.data;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class LocationPersistentDataType implements PersistentDataType<PersistentDataContainer, Location> {
    public static final LocationPersistentDataType INSTANCE = new LocationPersistentDataType();

    private static final NamespacedKey WORLD = new NamespacedKey(SchneckenPlugin.INSTANCE, "world");
    private static final NamespacedKey X = new NamespacedKey(SchneckenPlugin.INSTANCE, "x");
    private static final NamespacedKey Y = new NamespacedKey(SchneckenPlugin.INSTANCE, "y");
    private static final NamespacedKey Z = new NamespacedKey(SchneckenPlugin.INSTANCE, "z");
    private static final NamespacedKey YAW = new NamespacedKey(SchneckenPlugin.INSTANCE, "yaw");
    private static final NamespacedKey PITCH = new NamespacedKey(SchneckenPlugin.INSTANCE, "pitch");

    private LocationPersistentDataType() { }

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(final Location complex, final PersistentDataAdapterContext context) {
        final PersistentDataContainer container = context.newPersistentDataContainer();
        final World world = complex.getWorld();
        if (world != null) {
            container.set(WORLD, STRING, world.getName());
        }
        container.set(X, DOUBLE, complex.getX());
        container.set(Y, DOUBLE, complex.getY());
        container.set(Z, DOUBLE, complex.getZ());
        container.set(YAW, FLOAT, complex.getYaw());
        container.set(PITCH, FLOAT, complex.getPitch());
        return container;
    }

    @Override
    public Location fromPrimitive(final PersistentDataContainer primitive, final PersistentDataAdapterContext context) {
        final String worldName = primitive.get(WORLD, STRING);
        final World world = worldName != null ? Bukkit.getWorld(worldName) : null;
        final double x = primitive.get(X, DOUBLE);
        final double y = primitive.get(Y, DOUBLE);
        final double z = primitive.get(Z, DOUBLE);
        final float yaw = primitive.get(YAW, FLOAT);
        final float pitch = primitive.get(PITCH, FLOAT);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
