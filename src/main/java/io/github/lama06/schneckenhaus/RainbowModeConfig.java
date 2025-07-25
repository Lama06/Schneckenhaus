package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.config.CompoundConfig;
import io.github.lama06.schneckenhaus.config.ConfigAttribute;
import io.github.lama06.schneckenhaus.config.type.IntegerConfigType;
import io.github.lama06.schneckenhaus.config.type.PrimitiveConfigType;

import java.util.List;

public class RainbowModeConfig extends CompoundConfig {
    public boolean enabled;
    public int delay;

    @Override
    protected List<ConfigAttribute<?>> getAttributes() {
        return List.of(
            new ConfigAttribute<>(
                "enabled",
                PrimitiveConfigType.BOOLEAN,
                () -> enabled,
                v -> enabled = v
            ),
            new ConfigAttribute<>(
                "delay",
                IntegerConfigType.POSITIVE,
                () -> delay,
                v -> delay = v
            )
        );
    }
}
