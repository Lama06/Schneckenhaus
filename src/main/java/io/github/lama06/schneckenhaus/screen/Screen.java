package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.util.EventUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Screen implements Listener {
    protected final Player player;
    protected Inventory inventory;
    protected InventoryView view;

    private boolean open;
    private final Map<InventoryPosition, Consumer<ClickType>> callbacks = new HashMap<>();
    private boolean reopening;

    public Screen(Player player) {
        this.player = player;
    }

    protected abstract Component getTitle();

    protected abstract int getHeight();

    protected abstract void draw();

    protected void onClick(int x, int y, ClickType type) { }

    protected void onOpen() { }

    protected void onClose() { }

    public final void open() {
        if (open) {
            return;
        }
        open = true;

        inventory = Bukkit.createInventory(player, 9*getHeight(), getTitle());
        draw();
        view = player.openInventory(inventory);

        EventUtil.registerAll(this);

        onOpen();
    }

    public final void close() {
        if (!open) {
            return;
        }
        open = false;
        onClose();
        if (player.getOpenInventory().equals(view)) {
            view.close();
        }
        view = null;
        inventory = null;
        callbacks.clear();
        HandlerList.unregisterAll(this);
    }

    public final void redraw() {
        if (!open) {
            return;
        }
        inventory.clear();
        callbacks.clear();
        draw();
    }

    public final Player getPlayer() {
        return player;
    }

    protected final void setItem(int x, int y, ItemStack item, Consumer<ClickType> callback) {
        item.editMeta(meta -> {
            TextComponent noItalic = Component.empty().decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE);
            if (meta.hasCustomName()) {
                meta.customName(noItalic.append(meta.customName()));
            }
            if (meta.lore() != null) {
                meta.lore(
                    meta.lore().stream()
                        .map(noItalic::append)
                        .toList()
                );
            }
        });
        inventory.setItem(9*y + x, item);
        callbacks.put(new InventoryPosition(x, y), callback);
    }

    protected final void setItem(int x, int y, ItemStack item) {
        setItem(x, y, item, () -> { });
    }

    protected final void setItem(int x, int y, ItemStack item, Runnable callback) {
        setItem(x, y, item, click -> callback.run());
    }

    protected final void setItem(int x, int y, ScreenItem item) {
        if (item == null) {
            return;
        }
        setItem(x, y, item.item(), item.callback());
    }

    protected final void setItem(InventoryPosition position, ItemStack item, Runnable callback) {
        setItem(position.x(), position.y(), item, callback);
    }

    protected final void setItem(int slot, ItemStack item, Runnable callback) {
        setItem(InventoryPosition.fromSlot(slot), item, callback);
    }

    protected final void reopen() {
        inventory = Bukkit.createInventory(player, 9*getHeight(), getTitle());
        redraw();
        reopening = true;
        view = player.openInventory(inventory);
        reopening = false;
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!open) {
            return;
        }
        if (reopening) {
            return;
        }
        if (!event.getPlayer().equals(player)) {
            return;
        }
        close();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!open) {
            return;
        }
        if (!event.getPlayer().equals(player)) {
            return;
        }
        close();
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!open) {
            return;
        }
        if (!event.getWhoClicked().equals(player)) {
            return;
        }
        if (!inventory.equals(event.getClickedInventory())) {
            return;
        }
        int slot = event.getSlot();
        int y = slot / 9;
        int x = slot % 9;
        onClick(x, y, event.getClick());
        Consumer<ClickType> callback = callbacks.get(new InventoryPosition(x, y));
        if (callback != null) {
            callback.accept(event.getClick());
        }
        event.setCancelled(true);
    }
}