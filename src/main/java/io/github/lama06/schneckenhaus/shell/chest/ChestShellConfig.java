package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellConfig;
import org.bukkit.Material;

import java.util.List;

public final class ChestShellConfig extends SizedShellConfig {
    @Override
    protected List<ItemConfig> getDefaultIngredients() {
        return List.of(new ItemConfig(Material.CHEST), new ItemConfig(Material.SPYGLASS));
    }

    @Override
    protected ItemConfig getDefaultSizeIngredient() {
        return new ItemConfig(Material.DIAMOND);
    }

    @Override
    protected int getDefaultSizePerIngredient() {
        return 1;
    }
}
