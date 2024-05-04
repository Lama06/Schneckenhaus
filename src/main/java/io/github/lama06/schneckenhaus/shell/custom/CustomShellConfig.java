package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class CustomShellConfig extends ShellConfig {
    public static final Attribute<String> NAME = new Attribute<>("template", PersistentDataType.STRING);

    private final String name;

    public CustomShellConfig(final String name) {
        this.name = name;
    }

    @Override
    public void store(final PersistentDataContainer data) {
        NAME.set(data, name);
    }

    @Override
    protected Material getItemMaterial() {
        return Material.CHEST;
    }

    public String getName() {
        return name;
    }

    public CustomShellGlobalConfig getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getSchneckenConfig().custom.get(name);
    }
}
