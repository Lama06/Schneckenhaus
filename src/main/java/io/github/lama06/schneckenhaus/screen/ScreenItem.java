package io.github.lama06.schneckenhaus.screen;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public record ScreenItem(ItemStack item, Consumer<ClickType> callback) {
    public ScreenItem(ItemStack item, Runnable callback) {
        this(item, type -> callback.run());
    }
}