package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MapConfigType<T> implements ConfigType<Map<String, T>> {
    private final ConfigType<T> valueType;

    public MapConfigType(final ConfigType<T> valueType) {
        this.valueType = Objects.requireNonNull(valueType);
    }

    @Override
    public Map<String, T> parse(final Object data) throws ConfigException {
        if (!(data instanceof final Map<?, ?> map)) {
            throw new ConfigException("");
        }
        final Map<String, T> result = new LinkedHashMap<>();
        for (final Object key : map.keySet()) {
            if (!(key instanceof final String keyString)) {
                continue;
            }
            final T value;
            try {
                value = valueType.parse(map.get(key));
            } catch (final ConfigException exception) {
                exception.getPath().addPrefix(keyString);
                throw exception;
            }
            result.put(keyString, value);
        }
        return result;
    }

    @Override
    public void verify(final Map<String, T> data) throws ConfigException {
        for (final String key : data.keySet()) {
            try {
                valueType.verify(data.get(key));
            } catch (final ConfigException exception) {
                exception.getPath().addPrefix(key);
                throw exception;
            }
        }
    }

    @Override
    public Object store(final Map<String, T> data) {
        final Map<String, Object> result = new LinkedHashMap<>();
        for (final String key : data.keySet()) {
            result.put(key, valueType.store(data.get(key)));
        }
        return result;
    }
}
