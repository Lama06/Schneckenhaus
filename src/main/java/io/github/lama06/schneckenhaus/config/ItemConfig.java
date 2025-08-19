package io.github.lama06.schneckenhaus.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ItemConfig implements ComponentLike {
    public static ItemConfig parse(Object config) {
        if (config instanceof String string) {
            Material item = Registry.MATERIAL.get(NamespacedKey.fromString(string));
            if (item == null) {
                return null;
            }
            return new ItemConfig(item);
        }
        if (!(config instanceof Map<?, ?> map)) {
            return null;
        }
        if (!(map.get("item") instanceof String itemName)) {
            return null;
        }
        Material item = Registry.MATERIAL.get(NamespacedKey.fromString(itemName));
        if (item == null) {
            return null;
        }
        Integer model = null;
        if (map.get("model") instanceof Integer integer) {
            model = integer;
        }
        int amount = 1;
        if (map.get("amount") instanceof Integer integer) {
            amount = integer;
        }
        return new ItemConfig(item, amount, model);
    }

    public Material item;
    public int amount;
    public Integer model;

    public ItemConfig(Material item, int amount, Integer model) {
        this.item = item;
        this.model = model;
        this.amount = amount;
    }

    public ItemConfig(Material item) {
        this(item, 1, null);
    }

    public ItemConfig(ItemStack item) {
        this(item.getType(), item.getAmount(), null);
    }

    public boolean canRemoveFrom(int size, Function<Integer, ItemStack> get) {
        int remainingAmount = amount;
        for (int i = 0; i < size; i++) {
            ItemStack item = get.apply(i);
            if (item == null) {
                continue;
            }
            if (!hasMatchingTypeAndModelData(item)) {
                continue;
            }
            remainingAmount -= item.getAmount();
        }
        return remainingAmount <= 0;
    }

    public boolean canRemoveFrom(Inventory inventory) {
        return canRemoveFrom(inventory.getSize(), inventory::getItem);
    }

    public boolean removeFrom(int size, Function<Integer, ItemStack> get, BiConsumer<Integer, ItemStack> set) {
        if (!canRemoveFrom(size, get)) {
            return false;
        }
        int remainingAmount = amount;
        for (int i = 0; i < size; i++) {
            ItemStack item = get.apply(i);
            if (item == null) {
                continue;
            }
            if (!hasMatchingTypeAndModelData(item)) {
                continue;
            }
            if (item.getAmount() > remainingAmount) {
                item.setAmount(item.getAmount() - remainingAmount);
                return true;
            } else if (item.getAmount() == remainingAmount) {
                set.accept(i, null);
                return true;
            } else {
                remainingAmount -= item.getAmount();
                set.accept(i, null);
            }
        }
        return true;
    }

    public boolean removeFrom(Inventory inventory) {
        return removeFrom(inventory.getSize(), inventory::getItem, inventory::setItem);
    }

    private boolean hasMatchingTypeAndModelData(ItemStack item) {
        if (item.getType() != this.item) {
            return false;
        }
        if (model != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return false;
            }
            if (!meta.hasCustomModelData() || meta.getCustomModelData() != model) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Component asComponent() {
        return Component.text(amount + " ").append(Component.translatable(item));
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("item", item.getKey().toString());
        result.put("amount", amount);
        result.put("model", model);
        return result;
    }
}
