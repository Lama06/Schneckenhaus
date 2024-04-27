package io.github.lama06.schneckenhaus.data;

import org.bukkit.DyeColor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class EnumPersistentDataType<T extends Enum<T>> implements PersistentDataType<String, T> {
    public static final EnumPersistentDataType<DyeColor> DYE_COLOR = new EnumPersistentDataType<>(DyeColor.class);

    private final Class<T> type;

    private EnumPersistentDataType(final Class<T> type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<T> getComplexType() {
        return type;
    }

    @Override
    public String toPrimitive(final T complex, final PersistentDataAdapterContext context) {
        return complex.name();
    }

    @Override
    public T fromPrimitive(final String primitive, final PersistentDataAdapterContext context) {
        return Enum.valueOf(type, primitive);
    }
}
