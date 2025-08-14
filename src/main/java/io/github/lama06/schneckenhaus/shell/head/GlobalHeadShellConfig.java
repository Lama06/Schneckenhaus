package io.github.lama06.schneckenhaus.shell.head;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.builtin.GlobalBuiltinShellConfig;
import org.bukkit.Material;

import java.util.List;

public final class GlobalHeadShellConfig extends GlobalBuiltinShellConfig {
    @Override
    protected List<ItemConfig> getDefaultIngredients() {
        return List.of(new ItemConfig(Material.ZOMBIE_HEAD), new ItemConfig(Material.SPYGLASS));
    }
}
