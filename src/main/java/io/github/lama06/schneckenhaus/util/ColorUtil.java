package io.github.lama06.schneckenhaus.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class ColorUtil {
    private static final Map<Material, Color> BLOCKS = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            BLOCKS.put(MaterialUtil.getColoredConcrete(color), color.getColor());
        }
    }

    public static double difference(Color color1, Color color2) {
        return 0.3 * Math.pow(color1.getRed() - color2.getRed(), 2) +
            0.5 * Math.pow(color1.getGreen() - color2.getGreen(), 2) +
            0.1 * Math.pow(color1.getBlue() - color2.getBlue(), 2);
    }

    public static Material getMatchingBlockType(Color color) {
        return BLOCKS.entrySet().stream()
            .min(Comparator.comparingDouble(entry -> ColorUtil.difference(entry.getValue(), color)))
            .map(Map.Entry::getKey)
            .orElseThrow();
    }
}
