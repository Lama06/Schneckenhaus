package io.github.lama06.schneckenhaus.config.type;

import io.github.lama06.schneckenhaus.config.ConfigException;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;

import java.util.Objects;

public final class RegistryConfigType<T extends Keyed> implements ConfigType<T> {
    public static final RegistryConfigType<Material> MATERIAL = new RegistryConfigType<>(Registry.MATERIAL);

    private final Registry<T> registry;

    public RegistryConfigType(final Registry<T> registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public T parse(final Object data) throws ConfigException {
        if (!(data instanceof final String string)) {
            throw new ConfigException("Expected string: " + data);
        }
        final T keyed = registry.match(string);
        if (keyed == null) {
            throw new ConfigException("Unknown identifier: " + string);
        }
        return keyed;
    }

    @Override
    public Object store(final T keyed) {
        return keyed.getKey().toString();
    }
}
