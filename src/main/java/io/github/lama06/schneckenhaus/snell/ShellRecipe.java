package io.github.lama06.schneckenhaus.snell;

import org.bukkit.Material;

public abstract class ShellRecipe<C extends ShellConfig> {
    private final String id;
    private final Material material;

    protected ShellRecipe(final String id, final Material material) {
        this.id = id;
        this.material = material;
    }

    public abstract C getConfig(final int size);

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }
}
