package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.command.SchneckenCommand;
import io.github.lama06.schneckenhaus.recipe.RecipeManager;
import io.github.lama06.schneckenhaus.systems.Systems;
import io.github.lama06.schneckenhaus.update.ConfigurationUpdater;
import io.github.lama06.schneckenhaus.util.BuildProperties;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class SchneckenPlugin extends JavaPlugin {
    public static SchneckenPlugin INSTANCE;

    private BuildProperties buildProperties;
    private SchneckenWorld world;
    private SchneckenCommand command;
    private RecipeManager recipeManager;

    public SchneckenPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().setDefaults(new MemoryConfiguration());
        new ConfigurationUpdater().update();

        try {
            buildProperties = BuildProperties.load();
        } catch (final IOException e) {
            getLogger().log(Level.WARNING, "Failed to load build information", e);
            buildProperties = BuildProperties.FALLBACK;
        }
        world = new SchneckenWorld();
        recipeManager = new RecipeManager();
        recipeManager.registerRecipes();
        command = new SchneckenCommand();

        Systems.start();
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public BuildProperties getBuildProperties() {
        return buildProperties;
    }

    public SchneckenWorld getWorld() {
        return world;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public SchneckenCommand getCommand() {
        return command;
    }
}
