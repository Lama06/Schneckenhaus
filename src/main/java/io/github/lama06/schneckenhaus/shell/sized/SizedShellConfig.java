package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;

import java.util.Map;
import java.util.Objects;

public abstract class SizedShellConfig extends BuiltinShellConfig {
    private int initialCraftingSize = 4;
    private int maxCraftingSize = 16;
    private ItemConfig sizeIngredient = getDefaultSizeIngredient();
    private int sizePerIngredient = getDefaultSizePerIngredient();
    private int maxUpgradeSize = 16;
    private ItemConfig upgradeIngredient = getDefaultSizeIngredient();
    private int sizePerUpgradeIngredient = getDefaultSizePerIngredient();

    protected abstract ItemConfig getDefaultSizeIngredient();

    protected abstract int getDefaultSizePerIngredient();

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("initial_size") instanceof Integer integer) {
            initialCraftingSize = integer;
        }
        if (config.get("max_size") instanceof Integer integer) {
            maxCraftingSize = integer;
        }
        sizeIngredient = Objects.requireNonNullElse(ItemConfig.parse(config.get("size_ingredient")), sizeIngredient);
        if (config.get("size_per_ingredient") instanceof Integer integer) {
            sizePerIngredient = integer;
        }
        if (config.get("max_upgrade_size") instanceof Integer maxUpgradeSize) {
            this.maxUpgradeSize = maxUpgradeSize;
        }
        upgradeIngredient = Objects.requireNonNullElse(ItemConfig.parse(config.get("upgrade_ingredient")), upgradeIngredient);
        if (config.get("size_per_upgrade_ingredient") instanceof Integer integer) {
            sizePerUpgradeIngredient = integer;
        }
    }

    @Override
    protected void serialize(Map<String, Object> config) {
        super.serialize(config);
        config.put("initial_size", initialCraftingSize);
        config.put("max_size", maxCraftingSize);
        config.put("size_ingredient", sizeIngredient.serialize());
        config.put("size_per_ingredient", sizePerIngredient);
        config.put("max_upgrade_size", maxUpgradeSize);
        config.put("upgrade_ingredient", upgradeIngredient.serialize());
        config.put("size_per_upgrade_ingredient", sizePerUpgradeIngredient);
    }

    public int getInitialCraftingSize() {
        return initialCraftingSize;
    }

    public int getMaxCraftingSize() {
        return maxCraftingSize;
    }

    public ItemConfig getSizeIngredient() {
        return sizeIngredient;
    }

    public int getSizePerIngredient() {
        return sizePerIngredient;
    }

    public int getMaxUpgradeSize() {
        return maxUpgradeSize;
    }

    public ItemConfig getUpgradeIngredient() {
        return upgradeIngredient;
    }

    public int getSizePerUpgradeIngredient() {
        return sizePerUpgradeIngredient;
    }
}
