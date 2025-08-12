package io.github.lama06.schneckenhaus.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AnimationConfig extends ConditionalFeatureConfig {
    private boolean animateShells = true;
    private boolean animatePlacedShells = true;
    private int placedShellAnimationRange = 32;
    private boolean animateItems = true;
    private boolean animateDroppedItems = true;
    private int droppedItemsAnimationRange = 16;
    private int animationTaskDelay = 20;

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("animate_shells") instanceof Boolean animateShells) {
            this.animateShells = animateShells;
        }
        if (config.get("animate_placed_shells") instanceof Boolean animatePlacedShells) {
            this.animatePlacedShells = animatePlacedShells;
        }
        if (config.get("placed_shell_animation_range") instanceof Integer placedShellAnimationRange) {
            this.placedShellAnimationRange = placedShellAnimationRange;
        }
        if (config.get("animate_items") instanceof Boolean animateItems) {
            this.animateItems = animateItems;
        }
        if (config.get("animate_dropped_items") instanceof Boolean animateDroppedItems) {
            this.animateDroppedItems = animateDroppedItems;
        }
        if (config.get("dropped_items_animation_range") instanceof Integer droppedItemsAnimationRange) {
            this.droppedItemsAnimationRange = droppedItemsAnimationRange;
        }
        if (config.get("animation_task_delay") instanceof Integer animationTaskDelay) {
            this.animationTaskDelay = animationTaskDelay;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<>(super.serialize());
        config.put("animate_shells", animateShells);
        config.put("animate_placed_shells", animatePlacedShells);
        config.put("placed_shell_animation_range", placedShellAnimationRange);
        config.put("animate_items", animateItems);
        config.put("animate_dropped_items", animateDroppedItems);
        config.put("dropped_items_animation_range", droppedItemsAnimationRange);
        config.put("animation_task_delay", animationTaskDelay);
        return config;
    }

    public boolean isAnimateShells() {
        return animateShells;
    }

    public boolean isAnimatePlacedShells() {
        return animatePlacedShells;
    }

    public int getPlacedShellAnimationRange() {
        return placedShellAnimationRange;
    }

    public boolean isAnimateItems() {
        return animateItems;
    }

    public boolean isAnimateDroppedItems() {
        return animateDroppedItems;
    }

    public int getDroppedItemsAnimationRange() {
        return droppedItemsAnimationRange;
    }

    public int getAnimationTaskDelay() {
        return animationTaskDelay;
    }
}
