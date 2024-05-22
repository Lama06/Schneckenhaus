package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.config.CompoundConfig;
import io.github.lama06.schneckenhaus.config.ConfigAttribute;
import io.github.lama06.schneckenhaus.config.type.CompoundConfigType;
import io.github.lama06.schneckenhaus.config.type.MapConfigType;
import io.github.lama06.schneckenhaus.config.type.PrimitiveConfigType;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellGlobalConfig;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellGlobalConfig;

import java.util.List;
import java.util.Map;

public final class SchneckenConfig extends CompoundConfig {
    public boolean nesting;
    public BuiltinShellGlobalConfig shulker;
    public BuiltinShellGlobalConfig chest;
    public Map<String, CustomShellGlobalConfig> custom;

    @Override
    public List<ConfigAttribute<?>> getAttributes() {
        return List.of(
                new ConfigAttribute<>(
                        "nesting",
                        PrimitiveConfigType.BOOLEAN,
                        () -> nesting,
                        nesting -> this.nesting = nesting
                ),
                new ConfigAttribute<>(
                        "shulker",
                        new CompoundConfigType<>(BuiltinShellGlobalConfig::new),
                        () -> shulker,
                        shulker -> this.shulker = shulker
                ),
                new ConfigAttribute<>(
                        "chest",
                        new CompoundConfigType<>(BuiltinShellGlobalConfig::new),
                        () -> chest,
                        chest -> this.chest = chest
                ),
                new ConfigAttribute<>(
                        "custom",
                        new MapConfigType<>(new CompoundConfigType<>(CustomShellGlobalConfig::new)),
                        () -> custom,
                        custom -> this.custom = custom
                )
        );
    }
}
