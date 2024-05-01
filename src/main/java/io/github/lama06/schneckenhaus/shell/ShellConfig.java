package io.github.lama06.schneckenhaus.shell;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public abstract class ShellConfig {
    protected abstract Material getItemMaterial();

    protected ChatColor getItemColor() {
        return ChatColor.WHITE;
    }

    protected String getLore() {
        return null;
    }

    public final ItemStack createItem() {
        final ItemStack item = new ItemStack(getItemMaterial());
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(new ComponentBuilder("Snail Shell").color(getItemColor()).build().toLegacyText());
        final String lore = getLore();
        if (lore != null) {
            meta.setLore(List.of(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    public abstract void store(final PersistentDataContainer data);
}
