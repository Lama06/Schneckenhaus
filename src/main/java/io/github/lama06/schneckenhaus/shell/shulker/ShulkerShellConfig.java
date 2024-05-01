package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.EnumPersistentDataType;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

import java.awt.*;

public final class ShulkerShellConfig extends BuiltinShellConfig {
    public static final Attribute<DyeColor> COLOR = new Attribute<>("color", EnumPersistentDataType.DYE_COLOR);

    private DyeColor color;

    public ShulkerShellConfig() { }

    public ShulkerShellConfig(final int size, final DyeColor color) {
        super(size);
        this.color = color;
    }

    @Override
    public Material getItemMaterial() {
        return MaterialUtil.getColoredShulkerBox(color);
    }

    @Override
    public ChatColor getItemColor() {
        return ChatColor.of(new Color(getColor().getColor().asRGB()));
    }

    @Override
    public void store(final PersistentDataContainer data) {
        super.store(data);
        COLOR.set(data, color);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(final DyeColor color) {
        this.color = color;
    }
}
