package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.util.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ListConfigType<T> implements ConfigType<List<T>> {
    private final ConfigType<T> elementType;
    private final Range lengthRange;

    public ListConfigType(
            final ConfigType<T> elementType,
            final Range lengthRange
    ) {
        this.elementType = Objects.requireNonNull(elementType);
        this.lengthRange = Objects.requireNonNull(lengthRange);
    }

    public ListConfigType(final ConfigType<T> elementType) {
        this(elementType, Range.ALL);
    }

    @Override
    public List<T> parse(final Object data) throws ConfigException {
        if (!(data instanceof final List<?> list)) {
            throw new ConfigException("Expected list");
        }
        final List<T> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            final Object element = list.get(i);
            try {
                result.add(elementType.parse(element));
            } catch (final ConfigException exception) {
                exception.getPath().addPrefix(i);
                throw exception;
            }
        }
        return result;
    }

    @Override
    public void verify(final List<T> list) throws ConfigException {
        if (!lengthRange.contains(list.size())) {
            throw new ConfigException("Invalid length: %d is not %s".formatted(list.size(), lengthRange));
        }
        for (int i = 0; i < list.size(); i++) {
            final T element = list.get(i);
            try {
                elementType.verify(element);
            } catch (final ConfigException exception) {
                exception.getPath().addPrefix(i);
                throw exception;
            }
        }
    }

    @Override
    public Object store(final List<T> list) {
        final List<Object> result = new ArrayList<>();
        for (final T element : list) {
            result.add(elementType.store(element));
        }
        return result;
    }
}
