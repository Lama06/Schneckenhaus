package io.github.lama06.schneckenhaus.update;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public final class ConfigurationUpdater extends Updater<Configuration> {
    private final Configuration configuration = SchneckenPlugin.INSTANCE.getConfig();

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
                Map.entry(new PluginVersion(1, 1, 0), this::updateTo1_1_0)
        );
    }

    /*
    Config in 1.0.1:

    recipe:
      required_ingredients:
        - minecraft:spyglass
      size_ingredient: minecraft:gold_ingot
      initial_size: 3
      size_per_ingredient: 2
     */

    private void updateTo1_1_0() {
        configuration.set("nesting", false);

        final ConfigurationSection recipe = configuration.getConfigurationSection("recipe");
        final List<String> requiredIngredients = recipe.getStringList("required_ingredients");
        final String sizeIngredient = recipe.getString("size_ingredient");
        final int initialSize = recipe.getInt("initial_size");
        final int sizePerIngredient = recipe.getInt("size_per_ingredient");
        configuration.set("recipe", null);

        final ConfigurationSection shellTypes = configuration.createSection("shell_types");

        final ConfigurationSection shulker = shellTypes.createSection("shulker");
        shulker.set("enabled", true);
        final ConfigurationSection shulkerRecipe = shulker.createSection("recipe");
        shulkerRecipe.set("ingredients", requiredIngredients);
        shulkerRecipe.set("size_ingredient", sizeIngredient);
        shulkerRecipe.set("initial_size", initialSize);
        shulkerRecipe.set("size_per_ingredient", sizePerIngredient);

        final ConfigurationSection chest = shellTypes.createSection("chest");
        chest.set("enabled", true);
        final ConfigurationSection chestRecipe = chest.createSection("recipe");
        chestRecipe.set("ingredients", requiredIngredients);
        chestRecipe.set("size_ingredient", sizeIngredient);
        chestRecipe.set("initial_size", initialSize);
        chestRecipe.set("size_per_ingredient", sizePerIngredient);
    }

    /*
    Config in 1.1.0:
    data_version: 1.0.1

    nesting: true

    shell_types:
      shulker:
        enabled: true
        recipe:
          ingredients:
            - spyglass
          size_ingredient: gold_ingot
          initial_size: 4
          size_per_ingredient: 2
      chest:
        enabled: true
        recipe:
          ingredients:
            - spyglass
          size_ingredient: diamond
          initial_size: 4
          size_per_ingredient: 1
     */
}
