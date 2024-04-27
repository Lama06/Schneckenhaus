package io.github.lama06.schneckenhaus.snell;

import io.github.lama06.schneckenhaus.data.Attribute;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class ShellConfig {
    public static final Attribute<Integer> SIZE = new Attribute<>("size", PersistentDataType.INTEGER);

    private int size;

    public ShellConfig() { }

    public ShellConfig(final int size) {
        this.size = size;
    }

    public abstract Material getItemMaterial();

    public abstract ChatColor getItemColor();

    public final ItemStack createItem() {
        final ItemStack item = new ItemStack(getItemMaterial());
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(new ComponentBuilder("Snail Shell").color(getItemColor()).build().toLegacyText());
        item.setItemMeta(meta);
        return item;
    }

    public void store(final PersistentDataContainer data) {
        SIZE.set(data, size);
    }

    public final int getSize() {
        return size;
    }

    public final void setSize(final int size) {
        this.size = size;
    }
}
