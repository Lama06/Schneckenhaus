package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;

import java.util.Objects;

public abstract class ForwardingConfigType<T> implements ConfigType<T> {
    private final ConfigType<T> parent;

    public ForwardingConfigType(final ConfigType<T> parent) {
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public T parse(final Object data) throws ConfigException {
        return parent.parse(data);
    }

    @Override
    public void verify(final T data) throws ConfigException {
        parent.verify(data);
    }

    @Override
    public Object store(final T data) {
        return parent.store(data);
    }
}
