package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.EventUtil;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Screen extends ConstantsHolder implements Listener {
    protected final Player player;
    protected Inventory inventory;
    protected InventoryView view;

    private final Map<InventoryPosition, ItemRegistration> items = new HashMap<>();
    private BukkitTask tickTask;

    public Screen(Player player) {
        this.player = player;
    }

    protected abstract Component getTitle();

    protected abstract int getHeight();

    protected abstract void draw();

    protected void onOpen() { }

    protected void onClose() { }

    public final void open() {
        inventory = Bukkit.createInventory(player, 9*getHeight(), getTitle());
        draw();
        view = player.openInventory(inventory);

        EventUtil.registerAll(this);
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, 1);

        onOpen();
    }

    public final void close() {
        onClose();

        view.close();
        items.clear();
        tickTask.cancel();
        HandlerList.unregisterAll(this);
    }

    public final void redraw() {
        inventory.clear();
        items.clear();
        draw();
    }

    protected final void setItem(InventoryPosition position, Supplier<ItemStack> item, Integer animationDelay, Runnable callback) {
        if (position.getSlot() >= getHeight() * 9) {
            return;
        }
        inventory.setItem(position.getSlot(), InventoryUtil.removeDefaultFormatting(item.get()));
        items.put(position, new ItemRegistration(callback, item, animationDelay));
    }

    protected final void setItem(InventoryPosition position, Supplier<ItemStack> item, Integer animationDelay) {
        setItem(position, item, animationDelay, () -> { });
    }

    protected final void setItem(InventoryPosition position, ItemStack item, Runnable callback) {
        setItem(position, () -> item, null, callback);
    }

    protected final void setItem(InventoryPosition position, ItemStack item) {
        setItem(position, item, () -> { });
    }

    private void tick() {
        if (!player.isConnected() || !player.getOpenInventory().getTopInventory().equals(inventory)) {
            HandlerList.unregisterAll(this);
            tickTask.cancel();
            return;
        }
        for (InventoryPosition position : items.keySet()) {
            ItemRegistration itemRegistration = items.get(position);
            if (itemRegistration.animationDelay == null) {
                continue;
            }
            if (Bukkit.getCurrentTick() % itemRegistration.animationDelay != 0) {
                continue;
            }
            ItemStack item = itemRegistration.item().get();
            if (item == null) {
                continue;
            }
            inventory.setItem(position.getSlot(), InventoryUtil.removeDefaultFormatting(item));
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        tickTask.cancel();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        tickTask.cancel();
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(player)) {
            return;
        }
        if (!inventory.equals(event.getClickedInventory())) {
            return;
        }
        event.setCancelled(true);
        InventoryPosition position = InventoryPosition.fromSlot(event.getSlot());
        ItemRegistration item = items.get(position);
        if (item == null) {
            return;
        }
        item.callback.run();
    }

    private record ItemRegistration(
        Runnable callback,
        Supplier<ItemStack> item,
        Integer animationDelay
    ) { }
}