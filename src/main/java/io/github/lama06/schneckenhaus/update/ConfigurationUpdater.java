package io.github.lama06.schneckenhaus.update;

import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigurationUpdater extends Updater<Configuration> {
    private final Configuration configuration;

    public ConfigurationUpdater(final Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    @Override
    protected Configuration getData() {
        return configuration;
    }

    @Override
    protected PluginVersion getDataVersion() {
        final String dataVersion = configuration.getString("data_version", "1.0.1");
        return PluginVersion.fromString(dataVersion);
    }

    @Override
    protected void setDataVersion(final PluginVersion version) {
        configuration.set("data_version", version.toString());
    }

    @Override
    protected Map<PluginVersion, Runnable> getUpdates() {
        return Map.ofEntries(
            Map.entry(new PluginVersion(1, 1, 0), this::updateTo1_1_0),
            Map.entry(new PluginVersion(2, 0, 0), this::updateTo2_0_0),
            Map.entry(new PluginVersion(2, 1, 0), this::updateTo2_1_0),
            Map.entry(new PluginVersion(2, 2, 0), this::updateTo2_2_0)
        );
    }

    /*
    Config in 1.1.0:

    recipe:
      required_ingredients:
        - minecraft:spyglass
      size_ingredient: minecraft:gold_ingot
      initial_size: 3
      size_per_ingredient: 2
     */

    private void updateTo1_1_0() {
        configuration.set("nesting", false);
        configuration.createSection("custom");

        final ConfigurationSection recipe = configuration.getConfigurationSection("recipe");
        final List<String> requiredIngredients = recipe.getStringList("required_ingredients");
        final String sizeIngredient = recipe.getString("size_ingredient", "minecraft:gold_ingot");
        final int initialSize = recipe.getInt("initial_size");
        final int sizePerIngredient = recipe.getInt("size_per_ingredient");
        configuration.set("recipe", null);

        final ConfigurationSection shulker = configuration.createSection("shulker");
        shulker.set("enabled", true);
        shulker.set("ingredients", requiredIngredients);
        shulker.set("size_ingredient", sizeIngredient);
        shulker.set("initial_size", initialSize);
        shulker.set("size_per_ingredient", sizePerIngredient);

        final ConfigurationSection chest = configuration.createSection("chest");
        chest.set("enabled", true);
        chest.set("ingredients", requiredIngredients);
        chest.set("size_ingredient", sizeIngredient);
        chest.set("initial_size", initialSize);
        chest.set("size_per_ingredient", sizePerIngredient);
    }

    /*
    Config in 1.1.0:

    nesting: true

    shulker:
      enabled: true
      ingredients:
        - spyglass
      size_ingredient: gold_ingot
      initial_size: 4
      size_per_ingredient: 2
    chest:
      enabled: true
      ingredients:
        - spyglass
      size_ingredient: diamond
      initial_size: 4
      size_per_ingredient: 1
    custom: { }
     */

    private void updateTo2_0_0() {
        configuration.set("theft_prevention", false);

        ConfigurationSection homeShell = configuration.createSection("home_shell");
        homeShell.set("enabled", false);
        homeShell.set("size", 16);
        homeShell.set("command", true);
        homeShell.set("prevent_homelessness", true);
    }

    private void updateTo2_1_0() {
        configuration.set("rainbow_mode_delay", 5);
    }

    private void updateTo2_2_0() {
        int rainbowModeDelay = configuration.getInt("rainbow_mode_delay", 5);
        configuration.set("rainbow_mode_delay", null);

        ConfigurationSection rainbow = configuration.createSection("rainbow_mode");
        rainbow.set("enabled", true);
        rainbow.set("delay", rainbowModeDelay);
    }
}
