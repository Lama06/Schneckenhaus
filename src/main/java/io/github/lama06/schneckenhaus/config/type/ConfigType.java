package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;

public interface ConfigType<T> {
    T parse(final Object data) throws ConfigException;

    default void verify(final T data) throws ConfigException { }

    Object store(final T data);
}
