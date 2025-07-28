package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.shell.AccessMode;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class AccessControlScreen extends Screen {
    private final Shell<?> shell;

    public AccessControlScreen(Player player, Shell<?> shell) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Component.text(t("ui_access_control_title"), NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 1;
    }

    @Override
    protected void draw() {
        for (int i = 0; i < AccessMode.values().length; i++) {
            AccessMode accessMode = AccessMode.values()[i];
            ItemStack item = new ItemStack(accessMode.icon);
            item.editMeta(meta -> {
                meta.customName(accessMode.name);
                List<Component> lore = new ArrayList<>(accessMode.description);
                if (Shell.ACCESS_MODE.get(shell) == accessMode) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                    lore.add(Component.text(t("ui_selected"), NamedTextColor.GREEN));
                } else {
                    lore.add(Component.text(t("ui_click_to_select"), NamedTextColor.YELLOW));
                }
                meta.lore(lore);
            });
            setItem(i, item, () -> {
                Shell.ACCESS_MODE.set(shell, accessMode);
                redraw();
            });
        }

        ItemStack blacklist = new ItemStack(AccessMode.BLACKLIST.icon);
        blacklist.editMeta(meta -> {
            meta.customName(Component.text(t("ui_access_control_edit_blacklist"), NamedTextColor.YELLOW));
            if (Shell.ACCESS_MODE.get(shell) != AccessMode.BLACKLIST) {
                meta.lore(List.of(Component.text(t("ui_access_control_blacklist_disabled"), NamedTextColor.RED)));
            }
        });
        setItem(7, 0, blacklist, () -> new PlayerListEditScreen(
            player,
            Component.text(t("ui_access_control_edit_blacklist"), NamedTextColor.YELLOW),
            Shell.BLACKLIST.get(shell),
            list -> Shell.BLACKLIST.set(shell, list),
            () -> new AccessControlScreen(player, shell).open()
        ).open());

        ItemStack whitelist = new ItemStack(AccessMode.WHITELIST.icon);
        whitelist.editMeta(meta -> {
            meta.customName(Component.text(t("ui_access_control_edit_whitelist"), NamedTextColor.YELLOW));
            if (Shell.ACCESS_MODE.get(shell) != AccessMode.WHITELIST) {
                meta.lore(List.of(Component.text(t("ui_access_control_whitelist_disabled"), NamedTextColor.RED)));
            }
        });
        setItem(8, 0, whitelist, () -> new PlayerListEditScreen(
            player,
            Component.text(t("ui_access_control_edit_whitelist"), NamedTextColor.YELLOW),
            Shell.WHITELIST.get(shell),
            list -> Shell.WHITELIST.set(shell, list),
            () -> new AccessControlScreen(player, shell).open()
        ).open());
    }
}
