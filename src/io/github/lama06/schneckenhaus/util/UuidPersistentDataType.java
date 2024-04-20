package io.github.lama06.schneckenhaus.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * Stores {@link UUID}s in {@link PersistentDataContainer}s.
 */
public final class UuidPersistentDataType implements PersistentDataType<String, UUID> {
    public static final UuidPersistentDataType INSTANCE = new UuidPersistentDataType();

    private UuidPersistentDataType() { }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public String toPrimitive(final UUID complex, final PersistentDataAdapterContext context) {
        return complex.toString();
    }

    @Override
    public UUID fromPrimitive(final String primitive, final PersistentDataAdapterContext context) {
        return UUID.fromString(primitive);
    }
}
