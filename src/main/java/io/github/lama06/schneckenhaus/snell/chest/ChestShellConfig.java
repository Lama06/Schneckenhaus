package io.github.lama06.schneckenhaus.snell.chest;

import io.github.lama06.schneckenhaus.snell.ShellConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public final class ChestShellConfig extends ShellConfig {
    public ChestShellConfig() { }

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
