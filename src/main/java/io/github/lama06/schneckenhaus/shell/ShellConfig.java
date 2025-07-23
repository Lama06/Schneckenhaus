package io.github.lama06.schneckenhaus.shell;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public abstract class ShellConfig {
    protected abstract Material getItemMaterial();

    protected TextColor getItemColor() {
        return NamedTextColor.WHITE;
    }

    protected String getLore() {
        return null;
    }

    public final ItemStack createItem() {
        final ItemStack item = new ItemStack(getItemMaterial());
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Snail shell", getItemColor()));
        final String lore = getLore();
        if (lore != null) {
            meta.lore(List.of(Component.text(lore)));
        }
        item.setItemMeta(meta);
        return item;
    }

    public abstract void store(final PersistentDataContainer data);
}
