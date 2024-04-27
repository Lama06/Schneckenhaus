package io.github.lama06.schneckenhaus.data;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class Attribute<T> {
    private final NamespacedKey key;
    private final PersistentDataType<?, T> type;

    public Attribute(final String key, final PersistentDataType<?, T> type) {
        this.key = new NamespacedKey(SchneckenPlugin.INSTANCE, key);
        this.type = Objects.requireNonNull(type);
    }

    public T get(final PersistentDataHolder holder) {
        return get(holder.getPersistentDataContainer());
    }

    public T get(final PersistentDataContainer data) {
        return data.get(key, type);
    }

    public T getOrDefault(final PersistentDataHolder holder, final T fallback) {
        return getOrDefault(holder.getPersistentDataContainer(), fallback);
    }

    public T getOrDefault(final PersistentDataContainer data, final T fallback) {
        return data.getOrDefault(key, type, fallback);
    }

    public void set(final PersistentDataHolder holder, final T value) {
        set(holder.getPersistentDataContainer(), value);
    }

    public void set(final PersistentDataContainer data, final T value) {
        data.set(key, type, value);
    }

    public boolean has(final PersistentDataHolder holder) {
        return has(holder.getPersistentDataContainer());
    }

    public boolean has(final PersistentDataContainer data) {
        return data.has(key, type);
    }

    public NamespacedKey getKey() {
        return key;
    }

    public PersistentDataType<?, T> getType() {
        return type;
    }
}
