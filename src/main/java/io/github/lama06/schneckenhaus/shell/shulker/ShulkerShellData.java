package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.shell.sized.SizedShellData;
import org.bukkit.DyeColor;

import java.util.Set;

public interface ShulkerShellData extends SizedShellData {
    DyeColor getColor();

    boolean isRainbow();

    Set<DyeColor> getRainbowColors();
}
