package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.data.EnumPersistentDataType;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

public final class ShulkerShellConfig extends BuiltinShellConfig {
    public static final Attribute<DyeColor> COLOR = new Attribute<>("color", EnumPersistentDataType.DYE_COLOR);

    private final DyeColor color;

    public ShulkerShellConfig(final int size, final DyeColor color) {
        super(size);
        this.color = color;
    }

    @Override
    public Material getItemMaterial() {
        return MaterialUtil.getColoredShulkerBox(color);
    }

    @Override
    public TextColor getItemColor() {
        return TextColor.color(getColor().getColor().asRGB());
    }

    @Override
    public void store(final PersistentDataContainer data) {
        super.store(data);
        COLOR.set(data, color);
    }

    public DyeColor getColor() {
        return color;
    }
}
