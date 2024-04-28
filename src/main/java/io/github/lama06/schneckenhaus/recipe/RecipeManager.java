package io.github.lama06.schneckenhaus.recipe;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.snell.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RecipeManager {
    private final Map<NamespacedKey, RegisteredShellRecipe<?>> recipes = new HashMap<>();

    public Map<NamespacedKey, RegisteredShellRecipe<?>> getRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public void registerRecipes() {
        for (final ShellFactory<?> factory : ShellFactories.getFactories()) {
            registerRecipes(factory);
        }
    }

    private ConfigurationSection getConfig(final ShellFactory<?> factory) {
        return SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("shell_types").getConfigurationSection(factory.getPluginConfigName());
    }

    private ConfigurationSection getRecipeConfig(final ShellFactory<?> factory) {
        return getConfig(factory).getConfigurationSection("recipe");
    }

    private <C extends ShellConfig> void registerRecipes(final ShellFactory<C> factory) {
        final ConfigurationSection config = getConfig(factory);
        if (!config.getBoolean("enabled")) {
            return;
        }
        for (final ShellRecipe<C> recipe : factory.getRecipes()) {
            for (int size = 0; size <= getMaxSizeIngredientAmount(factory); size++) {
                registerRecipe(
                        "snail_shell_%s_%s_%d".formatted(factory.getName(), recipe.getId(), size),
                        factory,
                        recipe,
                        size
                );
            }
        }
    }

    private int getMaxSizeIngredientAmount(final ShellFactory<?> factory) {
        final ConfigurationSection recipeConfig = getRecipeConfig(factory);
        // ShapelessRecipe only allows up to nine ingredients.
        // Minus one because of the shell specific material (e.g. shulker box).
        return 9 - 1 - recipeConfig.getStringList("ingredients").size();
    }

    private <C extends ShellConfig> void registerRecipe(
            final String key,
            final ShellFactory<C> factory,
            final ShellRecipe<C> shellRecipe,
            final int sizeIngredientAmount
    ) {
        final ConfigurationSection recipeConfig = getRecipeConfig(factory);
        final int rawSize = recipeConfig.getInt("initial_size") + sizeIngredientAmount * recipeConfig.getInt("size_per_ingredient");
        final int size = Math.min(factory.getMaxSize(), Math.max(factory.getMinSize(), rawSize));

        final C config = shellRecipe.getConfig(size);
        final ItemStack result = config.createItem();

        final ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SchneckenPlugin.INSTANCE, key), result);
        recipe.addIngredient(shellRecipe.getMaterial());
        for (final String ingredientName : recipeConfig.getStringList("ingredients")) {
            final Material ingredient = Registry.MATERIAL.get(NamespacedKey.fromString(ingredientName));
            recipe.addIngredient(ingredient);
        }
        final Material sizeIngredient = Registry.MATERIAL.get(NamespacedKey.fromString(recipeConfig.getString("size_ingredient")));
        recipe.addIngredient(sizeIngredientAmount, sizeIngredient);

        Bukkit.addRecipe(recipe);
        recipes.put(recipe.getKey(), new RegisteredShellRecipe<>(factory, config));
    }

}
