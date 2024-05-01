package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import net.md_5.bungee.api.ChatColor;
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
    public ChatColor getItemColor() {
        return ChatColor.WHITE;
    }
}
