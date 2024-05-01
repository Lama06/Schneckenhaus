package io.github.lama06.schneckenhaus.recipe;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import org.bukkit.OfflinePlayer;

public record RegisteredShellRecipe<C extends ShellConfig>(ShellFactory<C> factory, C config) {
    public Shell<C> createShell(final OfflinePlayer creator) {
        return SchneckenPlugin.INSTANCE.getWorld().createShell(factory, creator, config);
    }
}
