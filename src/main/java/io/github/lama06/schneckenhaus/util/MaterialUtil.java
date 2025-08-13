package io.github.lama06.schneckenhaus.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public final class MaterialUtil {
    private static Material getColoredMaterial(String name, DyeColor color) {
        return Registry.MATERIAL.get(NamespacedKey.minecraft(color.name().toLowerCase() + "_" + name));
    }

    public static Material getColoredConcrete(DyeColor color) {
        return getColoredMaterial("concrete", color);
    }

    public static Material getColoredTerracotta(DyeColor color) {
        return getColoredMaterial("terracotta", color);
    }

    public static Material getColoredGlass(DyeColor color) {
        return getColoredMaterial("stained_glass", color);
    }

    public static Material getColoredGlassPane(DyeColor color) {
        return getColoredMaterial("stained_glass_pane", color);
    }

    public static Material getColoredShulkerBox(DyeColor color) {
        return getColoredMaterial("shulker_box", color);
    }

    public static Material getColoredDye(DyeColor color) {
        return getColoredMaterial("dye", color);
    }

    private MaterialUtil() { }
}
