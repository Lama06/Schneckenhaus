package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.command.SchneckenCommand;
import io.github.lama06.schneckenhaus.recipe.RecipeManager;
import io.github.lama06.schneckenhaus.systems.Systems;
import io.github.lama06.schneckenhaus.update.ConfigurationUpdater;
import io.github.lama06.schneckenhaus.util.BuildProperties;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
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
        new ConfigurationUpdater().update();
        copyComments();

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

        try {
            new Metrics(this, 21674);
        } catch (final RuntimeException e) {
            getLogger().warning("Failed to start bStats");
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void copyComments() {
        final FileConfiguration config = getConfig();
        final FileConfiguration defaults = (FileConfiguration) config.getDefaults();
        config.options().setHeader(defaults.options().getHeader());
        config.options().setFooter(defaults.options().getFooter());
        for (final String path : defaults.getKeys(true)) {
            config.setComments(path, defaults.getComments(path));
            config.setInlineComments(path, defaults.getInlineComments(path));
        }
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
