package io.github.lama06.schneckenhaus.config;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public final class ItemConfig {
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

    public boolean hasMatchingTypeAndModelData(ItemStack item) {
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

    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("item", item.getKey().toString());
        result.put("amount", amount);
        result.put("model", model);
        return result;
    }
}
