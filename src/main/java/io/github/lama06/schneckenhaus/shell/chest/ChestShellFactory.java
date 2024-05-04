package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellGlobalConfig;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellRecipe;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public final class ChestShellFactory extends BuiltinShellFactory<ChestShellConfig> {
    public static final ChestShellFactory INSTANCE = new ChestShellFactory();

    private ChestShellFactory() { }

    @Override
    public String getName() {
        return "chest";
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
    protected BuiltinShellGlobalConfig getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getSchneckenConfig().chest;
    }

    @Override
    protected List<BuiltinShellRecipe<ChestShellConfig>> getBuiltinRecipes() {
        return List.of(new BuiltinShellRecipe<>("default", Material.CHEST) {
            @Override
            public ChestShellConfig getConfig(final int size) {
                return new ChestShellConfig(size);
            }
        });
    }

    @Override
    public ChestShell instantiate(final GridPosition position, final ChestShellConfig config) {
        return new ChestShell(position, config);
    }

    @Override
    protected ChestShellConfig loadBuiltinConfig(final int size, final PersistentDataContainer data) {
        return new ChestShellConfig(size);
    }

    @Override
    protected ChestShellConfig parseBuiltinConfig(final int size, final CommandSender sender, final String[] args) {
        return new ChestShellConfig(size);
    }
}
