package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.util.MaterialUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Recipes {
    private final Map<NamespacedKey, Data> recipes = new HashMap<>();

    private int getMaxSizeIngredientAmount() {
        final ConfigurationSection recipeConfig = SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("recipe");
        return 9 - 1 - recipeConfig.getStringList("required_ingredients").size();
    }

    private void registerRecipe(
            final String key,
            final Material shulker,
            final DyeColor color,
            final int sizeIngredientAmount
    ) {
        final ConfigurationSection recipeConfig = SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("recipe");
        final int rawSize = recipeConfig.getInt("initial_size") + sizeIngredientAmount * recipeConfig.getInt("size_per_ingredient");
        final int size = Math.min(SnailShell.MAX_SIZE, Math.max(SnailShell.MIN_SIZE, rawSize));

        final ItemStack result = new ItemStack(shulker);
        final ItemMeta resultMeta = result.getItemMeta();
        resultMeta.setDisplayName("Snail Shell");
        resultMeta.setLore(List.of(size + "x" + size));
        result.setItemMeta(resultMeta);

        final ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SchneckenPlugin.INSTANCE, key), result);
        recipe.addIngredient(shulker);
        for (final String ingredientName : recipeConfig.getStringList("required_ingredients")) {
            final Material ingredient = Registry.MATERIAL.get(NamespacedKey.fromString(ingredientName));
            recipe.addIngredient(ingredient);
        }
        final Material sizeIngredient = Registry.MATERIAL.get(NamespacedKey.fromString(recipeConfig.getString("size_ingredient")));
        recipe.addIngredient(sizeIngredientAmount, sizeIngredient);

        Bukkit.addRecipe(recipe);
        recipes.put(recipe.getKey(), new Data(color, size));
    }

    public void registerRecipes() {
        final int maxSizeIngredientAmount = getMaxSizeIngredientAmount();
        for (int size = 0; size <= maxSizeIngredientAmount; size++) {
            registerRecipe(
                    "snail_shell_" + size,
                    Material.SHULKER_BOX,
                    DyeColor.PURPLE,
                    size
            );
        }
        for (final DyeColor color : DyeColor.values()) {
            final Material skulkerMaterial = MaterialUtil.getColoredShulkerBox(color);
            for (int size = 0; size <= maxSizeIngredientAmount; size++) {
                registerRecipe(
                        color.name().toLowerCase() + "_snail_shell_" + size,
                        skulkerMaterial,
                        color,
                        size
                );
            }
        }
    }

    public Map<NamespacedKey, Data> getRecipes() {
        return recipes;
    }

    public record Data(DyeColor color, int size) { }
}
