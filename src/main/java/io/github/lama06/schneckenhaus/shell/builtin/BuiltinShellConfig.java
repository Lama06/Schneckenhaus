package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class BuiltinShellConfig extends ShellConfig {
    public static final Attribute<Integer> SIZE = new Attribute<>("size", PersistentDataType.INTEGER);

    private int size;

    protected BuiltinShellConfig() { }

    protected BuiltinShellConfig(final int size) {
        this.size = size;
    }

    @Override
    public void store(final PersistentDataContainer data) {
        SIZE.set(data, size);
    }

    @Override
    protected final String getLore() {
        return "%dx%d".formatted(size, size);
    }

    public final int getSize() {
        return size;
    }

    public final void setSize(final int size) {
        this.size = size;
    }
}
