package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.CompoundConfig;
import io.github.lama06.schneckenhaus.config.ConfigException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class CompoundConfigType<T extends CompoundConfig> implements ConfigType<T> {
    private final Supplier<T> constructor;

    public CompoundConfigType(final Supplier<T> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    @Override
    public T parse(final Object data) throws ConfigException {
        if (!(data instanceof final Map<?, ?> map)) {
            throw new ConfigException("Expected map");
        }
        final Map<String, Object> stringMap = new HashMap<>();
        for (final Object key : map.keySet()) {
            if (!(key instanceof final String keyString)) {
                continue;
            }
            stringMap.put(keyString, map.get(key));
        }
        final T compound = constructor.get();
        compound.load(stringMap);
        return compound;
    }

    @Override
    public void verify(final T compound) throws ConfigException {
        compound.verify();
    }

    @Override
    public Object store(final T compound) {
        return compound.store();
    }
}
