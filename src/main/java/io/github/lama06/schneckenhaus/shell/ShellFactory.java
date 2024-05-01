package io.github.lama06.schneckenhaus.shell;

import io.github.lama06.schneckenhaus.position.GridPosition;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public abstract class ShellFactory<C extends ShellConfig> {
    public abstract String getName();

    public abstract List<ShellRecipe<C>> getRecipes();

    public abstract Shell<C> instantiate(final GridPosition position, final C config);

    public abstract C loadConfig(final PersistentDataContainer data);

    public abstract List<String> tabCompleteConfig(final CommandSender sender, final String[] args);

    public abstract C parseConfig(final CommandSender sender, final String[] args);

    public abstract List<String> getConfigCommandTemplates();
}
