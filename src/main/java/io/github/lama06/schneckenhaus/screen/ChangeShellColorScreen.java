package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellConfig;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ChangeShellColorScreen extends Screen {
    private final ShulkerShell shell;

    public ChangeShellColorScreen(Player player, ShulkerShell shell) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Component.text("Change Color", NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return (int) Math.ceil((DyeColor.values().length + 1) / 9f);
    }

    @Override
    protected void draw() {
        DyeColor[] colors = DyeColor.values();
        for (int i = 0; i < colors.length; i++) {
            DyeColor color = colors[i];
            ItemStack item = new ItemStack(MaterialUtil.getColoredGlassPane(color));
            item.editMeta(meta -> {
                meta.customName(Component.text(EnumUtil.beautifyName(color), TextColor.color(color.getColor().asRGB())));
                List<Component> lore = new ArrayList<>();
                if (shell.getColor() == color) {
                    lore.add(Component.text("Current color", NamedTextColor.GREEN));
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                } else {
                    lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
                }
                meta.lore(lore);
            });
            setItem(i, item, () -> {
                ShulkerShellConfig.COLOR.set(shell, color);
                close();
            });
        }

        if (SchneckenPlugin.INSTANCE.getSchneckenConfig().rainbow.enabled) {
            boolean rainbowEnabled = ShulkerShell.RAINBOW.getOrDefault(shell, false);
            ItemStack rainbowToggle = new ItemStack(Material.CLOCK);
            rainbowToggle.editMeta(meta -> {
                TextComponent rainbowLabel = Component.text()
                    .append(MiniMessage.miniMessage().deserialize("<rainbow>Rainbow Mode: </rainbow>"))
                    .append(Component.text(rainbowEnabled ? "On" : "Off", rainbowEnabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .build();
                meta.displayName(rainbowLabel);
                meta.lore(List.of(
                    Component.text("Make the color change automatically every %d seconds".formatted(
                        SchneckenPlugin.INSTANCE.getSchneckenConfig().rainbow.delay
                    )),
                    Component.text("Click to toggle on / off", NamedTextColor.YELLOW)
                ));
            });
            setItem(colors.length, rainbowToggle, () -> {
                ShulkerShell.RAINBOW.set(shell, !rainbowEnabled);
                redraw();
            });
        }
    }
}
