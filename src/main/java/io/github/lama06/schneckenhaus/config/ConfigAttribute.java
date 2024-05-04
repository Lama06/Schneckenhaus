package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.config.type.ConfigType;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigAttribute<T> {
    private final String name;
    private final ConfigType<T> type;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    public ConfigAttribute(
            final String name,
            final ConfigType<T> type,
            final Supplier<T> getter,
            final Consumer<T> setter
    ) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.getter = Objects.requireNonNull(getter);
        this.setter = Objects.requireNonNull(setter);
    }

    public void load(final Map<String, Object> section) throws ConfigException {
        final Object data = section.get(name);
        if (data == null) {
            throw new ConfigException("Missing attribute", name);
        }
        try {
            final T newValue = type.parse(data);
            type.verify(newValue);
            setter.accept(newValue);
        } catch (final ConfigException exception) {
            exception.getPath().addPrefix(new ConfigPath.Name(name));
            throw exception;
        }
    }

    public void verify() throws ConfigException {
        try {
            type.verify(getter.get());
        } catch (final ConfigException exception) {
            exception.getPath().addPrefix(name);
            throw exception;
        }
    }

    public void store(final Map<String, Object> section) {
        section.put(name, type.store(getter.get()));
    }
}
