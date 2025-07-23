package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public final class ChestShellConfig extends BuiltinShellConfig {
    public ChestShellConfig(final int size) {
        super(size);
    }

    @Override
    public Material getItemMaterial() {
        return Material.CHEST;
    }

    @Override
    public TextColor getItemColor() {
        return NamedTextColor.WHITE;
    }
}
