package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class CraftingInput {
    private final ItemStack[] matrix;

    public CraftingInput(CraftingInventory inventory) {
        ItemStack[] originalMatrix = inventory.getMatrix();
        matrix = new ItemStack[originalMatrix.length];
        for (int i = 0; i < matrix.length; i++) {
            if (originalMatrix[i] == null) {
                continue;
            }
            matrix[i] = originalMatrix[i].clone();
        }
    }

    public CraftingInput(ItemStack[] matrix) {
        this.matrix = matrix;
    }

    public boolean canRemove(ItemConfig ingredient) {
        return ingredient.canRemoveFrom(matrix.length, i -> matrix[i]);
    }

    public boolean remove(ItemConfig ingredient) {
        return ingredient.removeFrom(matrix.length, i -> matrix[i], (i, item) -> matrix[i] = item);
    }

    public boolean canRemove(Material material) {
        return canRemove(new ItemConfig(material));
    }

    public boolean remove(Material material) {
        return remove(new ItemConfig(material));
    }

    public CraftingInput copy() {
        return new CraftingInput(Arrays.stream(matrix).map(item -> item == null ? null : item.clone()).toArray(ItemStack[]::new));
    }

    public ItemStack[] getMatrix() {
        return matrix;
    }
}
