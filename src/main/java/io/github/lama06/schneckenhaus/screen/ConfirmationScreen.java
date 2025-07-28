package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class ConfirmationScreen extends Screen {
    private final String actionName;
    private final Runnable cancelCallback;
    private final Runnable confirmCallback;

    public ConfirmationScreen(Player player, String actionName, Runnable cancelCallback, Runnable confirmCallback) {
        super(player);
        this.actionName = actionName;
        this.cancelCallback = cancelCallback;
        this.confirmCallback = confirmCallback;
    }

    @Override
    protected Component getTitle() {
        return Component.text(t("ui_confirm_title") + actionName);
    }

    @Override
    protected int getHeight() {
        return 1;
    }

    @Override
    protected void draw() {
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        cancel.editMeta(meta -> {
            meta.customName(Component.text(t("ui_confirm_cancel"), NamedTextColor.RED));
        });
        setItem(0, 0, cancel, () -> {
            close();
            cancelCallback.run();
        });

        for (int x = 1; x < 8; x++) {
            setItem(x, 0, InventoryUtil.createMarginItem());
        }

        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        confirm.editMeta(meta -> {
            meta.customName(Component.text(t("ui_confirm_confirm"), NamedTextColor.GREEN));
        });
        setItem(8, 0, confirm, () -> {
            close();
            confirmCallback.run();
        });
    }
}
