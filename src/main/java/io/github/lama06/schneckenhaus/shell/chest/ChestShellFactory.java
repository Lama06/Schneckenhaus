package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.GlobalSizedShellConfig;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellFactory;
import org.bukkit.Material;

public final class ChestShellFactory extends SizedShellFactory {
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
    public GlobalSizedShellConfig getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getPluginConfig().getChest();
    }

    @Override
    public ChestShellBuilder newBuilder() {
        return new ChestShellBuilder();
    }

    @Override
    protected Material getItemType(ShellData data) {
        return Material.CHEST;
    }

    @Override
    public ChestShell loadShell(int id) {
        ChestShell shell = new ChestShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}
