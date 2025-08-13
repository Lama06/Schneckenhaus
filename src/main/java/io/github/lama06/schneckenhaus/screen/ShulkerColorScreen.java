package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShell;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ShulkerColorScreen extends Screen {
    private final ShulkerShell shell;

    public ShulkerColorScreen(Player player, ShulkerShell shell) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Message.COLOR.toComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 3;
    }

    @Override
    protected void draw() {
        for (int x = 0; x < 9; x++) {
            setItem(x, 0, InventoryUtil.createMarginItem());
        }

        addRainbowToggle();

        List<ScreenItem> items = new ArrayList<>();
        if (shell.isRainbow()) {
            addRainbowColorSelectionItems(items);
        } else {
            addColorSelectionItems(items);
        }

        int slot = 9;
        for (ScreenItem item : items) {
            if (slot == 18) {
                slot++;
            }
            setItem(slot++, item);
        }
    }

    private void addRainbowToggle() {
        if (!Permission.TOGGLE_RAINBOW_MODE.check(player)) {
            return;
        }
        ItemStack rainbowToggle = new ItemStack(Material.CLOCK);
        rainbowToggle.editMeta(meta -> {
            meta.customName(MiniMessage.miniMessage().deserialize("<rainbow>" + Message.RAINBOW_MODE)
                .append(Component.text(": "))
                .append(Message.getBool(shell.isRainbow()).toComponent())
            );
            meta.lore(List.of(Message.getClickToEnableDisable(!shell.isRainbow()).toComponent()));
            if (shell.isRainbow()) {
                meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        });
        setItem(4, 0, rainbowToggle, () -> {
            shell.setRainbow(!shell.isRainbow());
            redraw();
        });
    }

    private void addColorSelectionItems(List<ScreenItem> items) {
        for (DyeColor color : DyeColor.values()) {
            ItemStack item = new ItemStack(MaterialUtil.getColoredGlassPane(color));
            item.editMeta(meta -> {
                meta.customName(Message.getDyeColor(color).toComponent());
                meta.lore(List.of(
                    Message.getSelectedOrClickToSelect(color == shell.getColor()).toComponent()
                ));
            });
            items.add(new ScreenItem(item, () -> {
                shell.setColor(color);
                redraw();
            }));
        }
    }

    private void addRainbowColorSelectionItems(List<ScreenItem> items) {
        Set<DyeColor> rainbowColors = shell.getRainbowColors();
        for (DyeColor color : DyeColor.values()) {
            boolean enabled = rainbowColors.contains(color);
            Material material = enabled ? MaterialUtil.getColoredGlassPane(color) : Material.GLASS_PANE;
            ItemStack item = new ItemStack(material);
            item.editMeta(meta -> {
                meta.customName(Message.getDyeColor(color).toComponent(enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
                meta.lore(List.of(Message.getClickToEnableDisable(!enabled).toComponent(NamedTextColor.YELLOW)));
            });
            items.add(new ScreenItem(item, () -> {
                shell.setRainbowColor(color, !enabled);
                redraw();
            }));
        }
    }
}
