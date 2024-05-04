package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.util.Range;

import java.util.Objects;

public final class IntegerConfigType implements ConfigType<Integer> {
    public static final IntegerConfigType ALL = new IntegerConfigType(Range.ALL);
    public static final IntegerConfigType POSITIVE = new IntegerConfigType(Range.POSITIVE);

    private final Range range;

    public IntegerConfigType(final Range range) {
        this.range = Objects.requireNonNull(range);
    }

    @Override
    public Integer parse(final Object data) throws ConfigException {
        if (!(data instanceof final Integer integer)) {
            throw new ConfigException("Expected integer: " + data);
        }
        return integer;
    }

    @Override
    public void verify(final Integer data) throws ConfigException {
        if (!range.contains(data)) {
            throw new ConfigException("The integer isn't " + range);
        }
    }

    @Override
    public Object store(final Integer data) {
        return data;
    }
}
