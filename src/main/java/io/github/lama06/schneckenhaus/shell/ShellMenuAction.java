package io.github.lama06.schneckenhaus.shell;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ShellMenuAction {
    private Component name;
    private Material icon;
    private List<Component> description;

    protected ShellMenuAction(Component name, Material icon, Component... description) {
        this.name = name;
        this.icon = icon;
        this.description = Arrays.asList(description);
    }

    protected ShellMenuAction() { }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(icon);
        item.editMeta(meta -> {
            meta.customName(name);
            meta.lore(description.stream().filter(Objects::nonNull).toList());
        });
        return item;
    }

    public Integer getItemAnimationDelay() {
        return null;
    }

    public abstract void onClick();
}
