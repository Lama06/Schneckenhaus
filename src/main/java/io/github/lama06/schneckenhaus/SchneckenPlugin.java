package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.command.SchneckenCommand;
import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.recipe.RecipeManager;
import io.github.lama06.schneckenhaus.systems.Systems;
import io.github.lama06.schneckenhaus.update.ConfigurationUpdater;
import io.github.lama06.schneckenhaus.util.BuildProperties;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class SchneckenPlugin extends JavaPlugin {
    private static final int BSTATS_ID = 21674;
    public static SchneckenPlugin INSTANCE;

    private YamlConfiguration embeddedConfig;
    private SchneckenConfig schneckenConfig;

    private BuildProperties buildProperties;
    private SchneckenWorld world;
    private SchneckenCommand command;
    private RecipeManager recipeManager;

    public SchneckenPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        try {
            buildProperties = BuildProperties.load();
        } catch (final IOException e) {
            getLogger().log(Level.WARNING, "Failed to load build information", e);
            buildProperties = BuildProperties.FALLBACK;
        }

        if (!loadSchneckenConfig()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        saveSchneckenConfig(); // Apply updates, clean up comments etc.

        world = new SchneckenWorld();
        recipeManager = new RecipeManager();
        recipeManager.registerRecipes();
        command = new SchneckenCommand();

        Systems.start();

        try {
            startBstats();
        } catch (final RuntimeException exception) {
            getLogger().log(Level.WARNING, "Failed to start bStats", exception);
        }
    }

    private File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }

    private boolean loadSchneckenConfig() {
        embeddedConfig = YamlConfiguration.loadConfiguration(getTextResource("config.yml"));

        final YamlConfiguration configuration;
        if (Files.exists(getConfigFile().toPath())) {
            configuration = new YamlConfiguration();
            try {
                configuration.load(getConfigFile());
            } catch (final IOException | InvalidConfigurationException exception) {
                getLogger().log(Level.SEVERE, "Failed to load the config file", exception);
                return false;
            }
            final ConfigurationUpdater updater = new ConfigurationUpdater(configuration);
            updater.update();
        } else {
            configuration = YamlConfiguration.loadConfiguration(getTextResource("config.yml"));
            configuration.set("data_version", PluginVersion.current().toString());
        }

        schneckenConfig = new SchneckenConfig();
        try {
            schneckenConfig.load(extractConfigurationSectionData(configuration));
            schneckenConfig.verify();
        } catch (final ConfigException exception) {
            getLogger().log(Level.SEVERE, "Invalid configuration at %s".formatted(exception.getPath()), exception);
            return false;
        }
        return true;
    }

    /**
     * Converts a configuration object like a configuration section to a map by recursively traversing it.
     */
    private Object extractConfigurationData(final Object value) {
        if (value instanceof final ConfigurationSection section) {
            return extractConfigurationSectionData(section);
        } else if (value instanceof final List<?> list) {
            return extractConfigurationListData(list);
        }
        return value;
    }

    private Map<String, Object> extractConfigurationSectionData(final ConfigurationSection section) {
        final Map<String, Object> values = new LinkedHashMap<>();
        for (final String key : section.getKeys(false)) {
            values.put(key, extractConfigurationData(section.get(key)));
        }
        return values;
    }

    private List<Object> extractConfigurationListData(final List<?> list) {
        final List<Object> values = new ArrayList<>();
        for (final Object value : list) {
            values.add(extractConfigurationData(value));
        }
        return values;
    }

    private void putConfigurationData(final ConfigurationSection section, final String key, final Object data) {
        // We can't just set the maps directly but need to create manually configuration sections.
        // Otherwise, no comments can be attached to the map's entries.
        if (data instanceof final Map<?, ?> map) {
            final ConfigurationSection subSection = section.createSection(key);
            for (final Object subKey : map.keySet()) {
                if (!(subKey instanceof final String subKeyString)) {
                    continue;
                }
                putConfigurationData(subSection, subKeyString, map.get(subKey));
            }
            return;
        }
        section.set(key, data);
    }

    public void saveSchneckenConfig() {
        final YamlConfiguration configuration = new YamlConfiguration();

        final Map<String, Object> serializedConfig = schneckenConfig.store();
        for (final String key : serializedConfig.keySet()) {
            putConfigurationData(configuration, key, serializedConfig.get(key));
        }

        configuration.set("data_version", PluginVersion.current().toString());

        configuration.options().setHeader(embeddedConfig.options().getHeader());
        configuration.options().setFooter(embeddedConfig.options().getFooter());
        for (final String path : embeddedConfig.getKeys(true)) {
            configuration.setComments(path, embeddedConfig.getComments(path));
            configuration.setInlineComments(path, embeddedConfig.getInlineComments(path));
        }

        try {
            configuration.save(getConfigFile());
        } catch (final IOException exception) {
            getLogger().log(Level.SEVERE, "Failed to save the configuration file", exception);
        }
    }

    private void startBstats() {
        if (getBuildProperties().debug()) {
            return;
        }
        final Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new Metrics.SimplePie("custom_shell_types", () -> schneckenConfig.custom.isEmpty() ? "no" : "yes"));
        metrics.addCustomChart(new Metrics.SingleLineChart("shells", world::getNumberOfShells));
    }

    public SchneckenConfig getSchneckenConfig() {
        return schneckenConfig;
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
