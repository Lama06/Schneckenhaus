package io.github.lama06.schneckenhaus.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class InventoryUtil {
    public static void fillMargin(Inventory inventory, ItemStack item) {
        int width = 9;
        int height = inventory.getSize() / 9;

        // left
        for (int y = 0; y < height; y++) {
            inventory.setItem(y * width, item);
        }

        // right
        for (int y = 0; y < height; y++) {
            inventory.setItem(y * width + width - 1, item);
        }

        // top
        for (int x = 1; x < width - 1; x++) {
            inventory.setItem(x, item);
        }

        // bottom
        for (int x = 1; x < width - 1; x++) {
            inventory.setItem((height - 1) * 9 + x, item);
        }
    }

    public static void fillMargin(Inventory inventory) {
        fillMargin(inventory, createMarginItem());
    }

    public static ItemStack createMarginItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.empty());
        item.setItemMeta(itemMeta);
        return item;
    }

    private static Component removeDefaultFormatting(Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .colorIfAbsent(NamedTextColor.WHITE);
    }

    public static void removeDefaultFormatting(ItemStack item) {
        item.editMeta(meta -> {
            if (meta.hasCustomName()) {
                meta.customName(removeDefaultFormatting(meta.customName()));
            }
            if (meta.hasLore()) {
                meta.lore(meta.lore().stream()
                    .map(InventoryUtil::removeDefaultFormatting)
                    .toList()
                );
            }
        });
    }
}
