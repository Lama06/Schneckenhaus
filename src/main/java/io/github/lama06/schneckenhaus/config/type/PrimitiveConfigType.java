package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;

import java.util.Objects;

public final class PrimitiveConfigType<T> implements ConfigType<T> {
    public static final PrimitiveConfigType<Boolean> BOOLEAN = new PrimitiveConfigType<>(Boolean.class);

    private final Class<T> type;

    private PrimitiveConfigType(final Class<T> type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public T parse(final Object data) throws ConfigException {
        if (!type.isInstance(data)) {
            throw new ConfigException("Illegal data type: " + data);
        }
        return type.cast(data);
    }

    @Override
    public Object store(final T data) {
        return data;
    }
}
