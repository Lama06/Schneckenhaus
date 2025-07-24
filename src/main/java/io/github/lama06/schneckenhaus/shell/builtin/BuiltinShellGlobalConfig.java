package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.config.CompoundConfig;
import io.github.lama06.schneckenhaus.config.ConfigAttribute;
import io.github.lama06.schneckenhaus.config.type.IntegerConfigType;
import io.github.lama06.schneckenhaus.config.type.ListConfigType;
import io.github.lama06.schneckenhaus.config.type.PrimitiveConfigType;
import io.github.lama06.schneckenhaus.config.type.RegistryConfigType;
import io.github.lama06.schneckenhaus.util.Range;
import org.bukkit.Material;

import java.util.List;

public final class BuiltinShellGlobalConfig extends CompoundConfig {
    public boolean enabled;
    public List<Material> ingredients;
    public Material sizeIngredient;
    public int initialSize;
    public int sizePerIngredient;

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
                    "size_ingredient",
                    RegistryConfigType.MATERIAL,
                    () -> sizeIngredient,
                    sizeIngredient -> this.sizeIngredient = sizeIngredient
            ),
            new ConfigAttribute<>(
                    "initial_size",
                    IntegerConfigType.POSITIVE,
                    () -> initialSize,
                    initialSize -> this.initialSize = initialSize
            ),
            new ConfigAttribute<>(
                    "size_per_ingredient",
                    IntegerConfigType.POSITIVE,
                    () -> sizePerIngredient,
                    sizePerIngredient -> this.sizePerIngredient = sizePerIngredient
            )
        );
    }
}
