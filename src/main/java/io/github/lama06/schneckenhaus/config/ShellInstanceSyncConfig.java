package io.github.lama06.schneckenhaus.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ShellInstanceSyncConfig extends ConditionalTaskFeatureConfig {
    private boolean shells = true;
    private boolean placedShells = true;
    private int placedShellsRange = 32;
    private boolean items = true;
    private boolean droppedItems = true;
    private int droppedItemsRange = 16;

    public ShellInstanceSyncConfig() {
        super(20);
    }

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("shells") instanceof Boolean shells) {
            this.shells = shells;
        }
        if (config.get("placed_shells") instanceof Boolean placedShells) {
            this.placedShells = placedShells;
        }
        if (config.get("placed_shells_range") instanceof Integer placedShellsRange) {
            this.placedShellsRange = placedShellsRange;
        }
        if (config.get("items") instanceof Boolean items) {
            this.items = items;
        }
        if (config.get("dropped_items") instanceof Boolean droppedItems) {
            this.droppedItems = droppedItems;
        }
        if (config.get("dropped_items_range") instanceof Integer droppedItemsRange) {
            this.droppedItemsRange = droppedItemsRange;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<>(super.serialize());
        config.put("shells", shells);
        config.put("placed_shells", placedShells);
        config.put("placed_shells_range", placedShellsRange);
        config.put("items", items);
        config.put("dropped_items", droppedItems);
        config.put("dropped_items_range", droppedItemsRange);
        return config;
    }

    public boolean isShells() {
        return shells;
    }

    public boolean isPlacedShells() {
        return placedShells;
    }

    public int getPlacedShellsRange() {
        return placedShellsRange;
    }

    public boolean isItems() {
        return items;
    }

    public boolean isDroppedItems() {
        return droppedItems;
    }

    public int getDroppedItemsRange() {
        return droppedItemsRange;
    }
}
