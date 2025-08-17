package io.github.lama06.schneckenhaus.ui;

public record InventoryPosition(int x, int y) {
    public static InventoryPosition fromSlot(int slot) {
        return new InventoryPosition(slot % 9, slot / 9);
    }

    public int getSlot() {
        return 9 * y + x;
    }
}