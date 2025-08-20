package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermission;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class PermissionScreen extends Screen {
    private final ShellPermission permission;

    public PermissionScreen(Player player, ShellPermission permission) {
        super(player);
        this.permission = permission;
    }

    @Override
    protected Component getTitle() {
        return permission.getName().asComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 1;
    }

    @Override
    protected void draw() {
        for (int i = 0; i < ShellPermissionMode.values().length; i++) {
            ShellPermissionMode mode = ShellPermissionMode.values()[i];

            ItemStack item = new ItemStack(mode.getIcon());
            item.editMeta(meta -> {
                meta.customName(mode.asComponent());
                if (permission.getMode() == mode) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                    meta.lore(List.of(Message.SELECTED.asComponent(NamedTextColor.GREEN)));
                } else {
                    meta.lore(List.of(Message.CLICK_TO_SELECT.asComponent(NamedTextColor.YELLOW)));
                }
            });
            setItem(InventoryPosition.fromSlot(i), item, () -> {
                permission.setMode(mode);
                redraw();
            });
        }

        ItemStack blacklist = new ItemStack(ShellPermissionMode.BLACKLIST.getIcon());
        blacklist.editMeta(meta -> {
            meta.customName(Message.EDIT_BLACKLIST.asComponent(NamedTextColor.YELLOW));
            if (permission.getMode() != ShellPermissionMode.BLACKLIST) {
                meta.lore(List.of(Message.BLACKLIST_DISABLED.asComponent(NamedTextColor.RED)));
            }
        });
        setItem(new InventoryPosition(7, 0), blacklist, () -> new PlayerListEditScreen(
            player,
            Message.EDIT_BLACKLIST.asComponent(NamedTextColor.YELLOW),
            permission.getBlacklist().get(),
            permission.getBlacklist()::set,
            () -> new PermissionScreen(player, permission).open()
        ).open());

        ItemStack whitelist = new ItemStack(ShellPermissionMode.WHITELIST.getIcon());
        whitelist.editMeta(meta -> {
            meta.customName(Message.EDIT_WHITELIST.asComponent(NamedTextColor.YELLOW));
            if (permission.getMode() != ShellPermissionMode.WHITELIST) {
                meta.lore(List.of(Message.WHITELIST_DISABLED.asComponent(NamedTextColor.RED)));
            }
        });
        setItem(new InventoryPosition(8, 0), whitelist, () -> new PlayerListEditScreen(
            player,
            Message.WHITELIST.asComponent(NamedTextColor.YELLOW),
            permission.getWhitelist().get(),
            permission.getWhitelist()::set,
            () -> new PermissionScreen(player, permission).open()
        ).open());
    }
}
