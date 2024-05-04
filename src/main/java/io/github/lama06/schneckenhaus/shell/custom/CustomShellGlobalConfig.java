package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.config.*;
import io.github.lama06.schneckenhaus.config.type.*;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.Range;
import org.bukkit.Material;

import java.util.List;

public final class CustomShellGlobalConfig extends CompoundConfig {
    public boolean enabled;
    public List<Material> ingredients;
    public BlockArea template;

    public CustomShellGlobalConfig(
            final boolean enabled,
            final List<Material> ingredients,
            final BlockArea template
    ) {
        this.enabled = enabled;
        this.ingredients = ingredients;
        this.template = template;
    }

    public CustomShellGlobalConfig() { }

    @Override
    public List<ConfigAttribute<?>> getAttributes() {
        return List.of(
                new ConfigAttribute<>(
                        "enabled",
                        PrimitiveConfigType.BOOLEAN,
                        () -> enabled,
                        enabled -> this.enabled = enabled
                ),
                new ConfigAttribute<>(
                        "ingredients",
                        new ListConfigType<>(RegistryConfigType.MATERIAL, new Range(1, 8)),
                        () -> ingredients,
                        ingredients -> this.ingredients = ingredients
                ),
                new ConfigAttribute<>(
                        "template",
                        new ForwardingConfigType<>(new BlockAreaConfigType(new Range(1, GridPosition.CELL_SIZE))) {
                            @Override
                            public void verify(final BlockArea data) throws ConfigException {
                                super.verify(data);
                                final BlockPosition upperCorner = data.getUpperCorner();
                                if (upperCorner.x() >= 0 || upperCorner.z() >= 0) {
                                    throw new ConfigException("The template's x- and y-coordinates must be < 0");
                                }
                            }
                        },
                        () -> template,
                        template -> this.template = template
                )
        );
    }
}
