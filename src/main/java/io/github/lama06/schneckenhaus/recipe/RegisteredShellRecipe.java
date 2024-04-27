package io.github.lama06.schneckenhaus.recipe;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.snell.Shell;
import io.github.lama06.schneckenhaus.snell.ShellConfig;
import io.github.lama06.schneckenhaus.snell.ShellFactory;
import org.bukkit.OfflinePlayer;

public record RegisteredShellRecipe<C extends ShellConfig>(ShellFactory<C> factory, C config) {
    public Shell createShell(final OfflinePlayer creator) {
        return SchneckenPlugin.INSTANCE.getWorld().createShell(factory, creator, config);
    }
}
