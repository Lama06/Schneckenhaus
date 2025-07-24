package io.github.lama06.schneckenhaus.screen;

public record InventoryPosition(int x, int y) {
    public static InventoryPosition fromSlot(int width, int slot) {
        return new InventoryPosition(slot % width, slot / width);
    }

    public static InventoryPosition fromSlot(int slot) {
        return fromSlot(9, slot);
    }
}