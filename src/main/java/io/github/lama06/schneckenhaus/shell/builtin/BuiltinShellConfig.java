package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.config.ItemConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BuiltinShellConfig {
    private boolean crafting = true;
    private List<ItemConfig> ingredients = getDefaultIngredients();

    protected List<ItemConfig> getDefaultIngredients() {
        return List.of();
    }

    public void deserialize(Map<?, ?> config) {
        if (config.get("crafting") instanceof Boolean bool) {
            crafting = bool;
        }
        if (config.get("ingredients") instanceof List<?> list) {
            ingredients = new ArrayList<>();
            for (Object ingredient : list) {
                ItemConfig parsedIngredient = ItemConfig.parse(ingredient);
                if (parsedIngredient != null) {
                    ingredients.add(parsedIngredient);
                }
            }
        }
    }

    protected void serialize(Map<String, Object> config) {
        config.put("crafting", crafting);
        config.put("ingredients", ingredients.stream().map(ItemConfig::serialize).toList());
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();
        serialize(config);
        return config;
    }

    public boolean isCrafting() {
        return crafting;
    }

    public List<ItemConfig> getIngredients() {
        return ingredients;
    }
}
