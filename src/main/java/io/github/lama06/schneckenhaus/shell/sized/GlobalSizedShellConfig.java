package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.builtin.GlobalBuiltinShellConfig;

import java.util.Map;
import java.util.Objects;

public abstract class GlobalSizedShellConfig extends GlobalBuiltinShellConfig {
    private int initialSize = 4;
    private int maxSize = 30;
    private ItemConfig sizeIngredient = getDefaultSizeIngredient();
    private int sizePerIngredient = getDefaultSizePerIngredient();
    private ItemConfig upgradeIngredient = getDefaultSizeIngredient();
    private int sizePerUpgradeIngredient = getDefaultSizePerIngredient();

    protected abstract ItemConfig getDefaultSizeIngredient();

    protected abstract int getDefaultSizePerIngredient();

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("initial_size") instanceof Integer integer) {
            initialSize = integer;
        }
        if (config.get("max_size") instanceof Integer integer) {
            maxSize = integer;
        }
        sizeIngredient = Objects.requireNonNullElse(ItemConfig.parse(config.get("size_ingredient")), sizeIngredient);
        if (config.get("size_per_ingredient") instanceof Integer integer) {
            sizePerIngredient = integer;
        }
        upgradeIngredient = Objects.requireNonNullElse(ItemConfig.parse(config.get("upgrade_ingredient")), upgradeIngredient);
        if (config.get("size_per_upgrade_ingredient") instanceof Integer integer) {
            sizePerUpgradeIngredient = integer;
        }
    }

    @Override
    protected void serialize(Map<String, Object> config) {
        super.serialize(config);
        config.put("initial_size", initialSize);
        config.put("max_size", maxSize);
        config.put("size_ingredient", sizeIngredient.serialize());
        config.put("size_per_ingredient", sizePerIngredient);
        config.put("upgrade_ingredient", upgradeIngredient.serialize());
        config.put("size_per_upgrade_ingredient", sizePerUpgradeIngredient);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ItemConfig getSizeIngredient() {
        return sizeIngredient;
    }

    public int getSizePerIngredient() {
        return sizePerIngredient;
    }

    public ItemConfig getUpgradeIngredient() {
        return upgradeIngredient;
    }

    public int getSizePerUpgradeIngredient() {
        return sizePerUpgradeIngredient;
    }
}
