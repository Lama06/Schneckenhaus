package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import org.bukkit.*;

import java.util.*;

public final class GlobalCustomShellConfig {
    public static GlobalCustomShellConfig deserialize(Map<?, ?> config) {
        GlobalCustomShellConfig result = new GlobalCustomShellConfig();

        if (config.get("crafting") instanceof Boolean crafting) {
            result.crafting = crafting;
        }

        if (config.get("item") instanceof String item) {
            result.item = Objects.requireNonNullElse(Registry.MATERIAL.get(NamespacedKey.fromString(item)), result.item);
        }

        if (config.get("ingredients") instanceof List<?> list) {
            for (Object ingredientConfig : list) {
                ItemConfig ingredient = ItemConfig.parse(ingredientConfig);
                if (ingredient != null) {
                    result.ingredients.add(ingredient);
                }
            }
        }

        if (!(config.get("template_world") instanceof String world)) {
            return null;
        }
        result.templateWorld = Bukkit.getWorld(world);
        if (result.templateWorld == null) {
            return null;
        }

        if (!(config.get("template_position") instanceof String position)) {
            return null;
        }
        result.templatePosition = BlockArea.fromString(position);
        if (result.templatePosition == null) {
            return null;
        }

        return result;
    }

    private boolean crafting = true;
    private Material item = Material.CHEST;
    private List<ItemConfig> ingredients = new ArrayList<>();
    private World templateWorld;
    private BlockArea templatePosition;

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("crafting", crafting);
        result.put("item", item.getKey().toString());
        result.put("ingredients", ingredients.stream().map(ItemConfig::serialize).toList());
        result.put("template_world", templateWorld.getName());
        result.put("template_position", templatePosition.toString());
        return result;
    }

    public boolean isCrafting() {
        return crafting;
    }

    public Material getItem() {
        return item;
    }

    public List<ItemConfig> getIngredients() {
        return ingredients;
    }

    public World getTemplateWorld() {
        return templateWorld;
    }

    public BlockArea getTemplatePosition() {
        return templatePosition;
    }
}
