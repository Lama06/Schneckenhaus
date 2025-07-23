package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.config.CompoundConfig;
import io.github.lama06.schneckenhaus.config.ConfigAttribute;
import io.github.lama06.schneckenhaus.config.type.IntegerConfigType;
import io.github.lama06.schneckenhaus.config.type.PrimitiveConfigType;

import java.util.List;

public final class HomeShellConfig extends CompoundConfig {
    public boolean enabled;
    public int size;
    public boolean command;
    public boolean preventHomelessness;

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
                "size",
                IntegerConfigType.POSITIVE,
                () -> size,
                v -> size = v
            ),
            new ConfigAttribute<>(
                "command",
                PrimitiveConfigType.BOOLEAN,
                () -> command,
                v -> command = v
            ),
            new ConfigAttribute<>(
                "prevent_homelessness",
                PrimitiveConfigType.BOOLEAN,
                () -> preventHomelessness,
                v -> preventHomelessness = v
            )
        );
    }
}
