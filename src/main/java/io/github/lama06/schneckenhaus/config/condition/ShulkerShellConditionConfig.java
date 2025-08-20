package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellData;
import org.bukkit.DyeColor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ShulkerShellConditionConfig extends SizedShellConditionConfig {
    public static final String TYPE = "shulker";

    private Set<DyeColor> colors;
    private Boolean rainbow;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return super.check(data) &&
            data instanceof ShulkerShellData shulkerData &&
            (colors == null || colors.contains(shulkerData.getColor())) &&
            (rainbow == null || rainbow == shulkerData.isRainbow());
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (!super.deserialize(config)) {
            return false;
        }
        if (config.get("colors") instanceof List<?> colors) {
            this.colors = colors.stream()
                .filter(name -> name instanceof String).map(name -> (String) name)
                .map(String::toUpperCase)
                .map(String::strip)
                .map(name -> {
                    try {
                        return DyeColor.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
        if (config.get("rainbow") instanceof Boolean rainbow) {
            this.rainbow = rainbow;
        }
        return true;
    }

    @Override
    public void serialize(Map<String, Object> result) {
        super.serialize(result);
        result.put("colors", colors == null ? null : colors.stream().sorted().map(Enum::name).map(String::toLowerCase).toList());
        result.put("rainbow", rainbow);
    }
}
