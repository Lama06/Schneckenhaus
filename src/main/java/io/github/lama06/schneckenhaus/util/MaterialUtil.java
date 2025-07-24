package io.github.lama06.schneckenhaus.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public final class MaterialUtil {
    private static Material getColoredMaterial(final String name, final DyeColor color) {
        return Registry.MATERIAL.get(NamespacedKey.minecraft(color.name().toLowerCase() + "_" + name));
    }

    public static Material getColoredConcrete(final DyeColor color) {
        return getColoredMaterial("concrete", color);
    }

    public static Material getColoredTerracotta(final DyeColor color) {
        return getColoredMaterial("terracotta", color);
    }

    public static Material getColoredGlass(final DyeColor color) {
        return getColoredMaterial("stained_glass", color);
    }

    public static Material getColoredGlassPane(final DyeColor color) {
        return getColoredMaterial("stained_glass_pane", color);
    }

    public static Material getColoredShulkerBox(final DyeColor color) {
        return getColoredMaterial("shulker_box", color);
    }

    private MaterialUtil() { }
}
