package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.sized.GlobalSizedShellConfig;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GlobalShulkerShellConfig extends GlobalSizedShellConfig {
    private boolean rainbowMode = true;
    private int rainbowDelay = 3 * 20;
    private ItemConfig rainbowIngredient = new ItemConfig(Material.CLOCK);

    @Override
    protected List<ItemConfig> getDefaultIngredients() {
        return List.of(new ItemConfig(Material.SPYGLASS));
    }

    @Override
    protected ItemConfig getDefaultSizeIngredient() {
        return new ItemConfig(Material.GOLD_INGOT);
    }

    @Override
    protected int getDefaultSizePerIngredient() {
        return 2;
    }

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("rainbow_mode") instanceof Boolean bool) {
            rainbowMode = bool;
        }
        if (config.get("rainbow_delay") instanceof Integer integer) {
            rainbowDelay = integer;
        }
        rainbowIngredient = Objects.requireNonNullElse(ItemConfig.parse(config.get("rainbow_ingredient")), rainbowIngredient);
    }

    @Override
    protected void serialize(Map<String, Object> config) {
        super.serialize(config);
        config.put("rainbow_mode", rainbowMode);
        config.put("rainbow_delay", rainbowDelay);
        config.put("rainbow_ingredient", rainbowIngredient.serialize());
    }

    public boolean isRainbowMode() {
        return rainbowMode;
    }

    public int getRainbowDelay() {
        return rainbowDelay;
    }

    public ItemConfig getRainbowIngredient() {
        return rainbowIngredient;
    }
}
