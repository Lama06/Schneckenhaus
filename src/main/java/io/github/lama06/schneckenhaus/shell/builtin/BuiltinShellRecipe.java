package io.github.lama06.schneckenhaus.shell.builtin;

import org.bukkit.Material;

public abstract class BuiltinShellRecipe<C extends BuiltinShellConfig> {
    private final String key;
    private final Material material;

    protected BuiltinShellRecipe(final String key, final Material material) {
        this.key = key;
        this.material = material;
    }

    public abstract C getConfig(final int size);

    public String getKey() {
        return key;
    }

    public Material getIngredient() {
        return material;
    }
}
