package io.github.lama06.schneckenhaus.shell.head;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import org.bukkit.Material;

import java.util.List;

public final class HeadShellConfig extends BuiltinShellConfig {
    @Override
    protected List<ItemConfig> getDefaultIngredients() {
        return List.of(new ItemConfig(Material.ZOMBIE_HEAD), new ItemConfig(Material.SPYGLASS));
    }
}
