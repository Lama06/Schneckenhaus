package io.github.lama06.schneckenhaus.recipe;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        for (final NamespacedKey key : recipes.keySet()) {
            Bukkit.removeRecipe(key);
        }
        recipes.clear();
        for (final ShellFactory<?> factory : ShellFactories.getFactories()) {
            registerRecipes(factory);
        }
    }

    private <C extends ShellConfig> void registerRecipes(final ShellFactory<C> factory) {
        for (final ShellRecipe<C> recipe : factory.getRecipes()) {
            final ItemStack result = recipe.config().createItem();
            final NamespacedKey key = new NamespacedKey(SchneckenPlugin.INSTANCE, factory.getName() + "_" + recipe.key());
            final ShapelessRecipe bukkitRecipe = new ShapelessRecipe(key, result);
            for (final Material ingredient : recipe.ingredients()) {
                bukkitRecipe.addIngredient(ingredient);
            }
            recipes.put(key, new RegisteredShellRecipe<>(factory, recipe.config()));
            Bukkit.addRecipe(bukkitRecipe);
        }
    }
}
