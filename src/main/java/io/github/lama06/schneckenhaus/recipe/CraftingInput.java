package io.github.lama06.schneckenhaus.recipe;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class CraftingInput {
    private final List<ItemStack> items;

    public CraftingInput(CraftingInventory inventory) {
        items = new ArrayList<>();
        for (ItemStack item : inventory.getMatrix()) {
            if (item != null) {
                items.add(item.clone());
            }
        }
    }

    public CraftingInput(List<ItemStack> items) {
        this.items = items;
    }

    private boolean canRemove(ItemConfig ingredient) {
        int remainingAmount = ingredient.amount;
        for (ItemStack item : items) {
            if (!ingredient.hasMatchingTypeAndModelData(item)) {
                continue;
            }
            remainingAmount -= item.getAmount();
        }
        return remainingAmount <= 0;
    }

    public boolean remove(ItemConfig ingredient) {
        if (!canRemove(ingredient)) {
            return false;
        }
        int remainingAmount = ingredient.amount;
        Iterator<ItemStack> iterator = items.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (!ingredient.hasMatchingTypeAndModelData(item)) {
                continue;
            }
            if (item.getAmount() > remainingAmount) {
                item.setAmount(item.getAmount() - remainingAmount);
            } else if (item.getAmount() == remainingAmount) {
                iterator.remove();
                return true;
            } else {
                remainingAmount -= item.getAmount();
                iterator.remove();
            }
        }
        return true;
    }

    public boolean remove(Material material) {
        return remove(new ItemConfig(material));
    }

    public CraftingInput copy() {
        List<ItemStack> items = this.items.stream().map(ItemStack::clone).collect(Collectors.toCollection(ArrayList::new));
        return new CraftingInput(items);
    }
}
