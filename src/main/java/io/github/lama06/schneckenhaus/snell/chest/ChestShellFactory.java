package io.github.lama06.schneckenhaus.snell.chest;

import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.snell.Shell;
import io.github.lama06.schneckenhaus.snell.ShellFactory;
import io.github.lama06.schneckenhaus.snell.ShellRecipe;
import org.bukkit.Material;

import java.util.Set;

public final class ChestShellFactory extends ShellFactory<ChestShellConfig> {
    public static final ChestShellFactory INSTANCE = new ChestShellFactory();

    private ChestShellFactory() { }

    @Override
    public String getName() {
        return "chest";
    }

    @Override
    public String getPluginConfigName() {
        return "chest";
    }

    @Override
    public Set<ShellRecipe<ChestShellConfig>> getRecipes() {
        return Set.of(new ShellRecipe<>("default", Material.CHEST) {
            @Override
            public ChestShellConfig getConfig(final int size) {
                return new ChestShellConfig(size);
            }
        });
    }

    @Override
    public Shell instantiate(final GridPosition position, final ChestShellConfig config) {
        return new ChestShell(position, config);
    }

    @Override
    public int getMinSize() {
        return 4;
    }

    @Override
    public int getMaxSize() {
        return 32 - 2;
    }

    @Override
    protected ChestShellConfig instantiateConfig() {
        return new ChestShellConfig();
    }
}
