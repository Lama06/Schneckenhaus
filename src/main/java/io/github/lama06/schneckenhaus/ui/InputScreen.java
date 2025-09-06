package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public final class InputScreen implements Listener {
    public static void openPlayerNameInput(
        Player player,
        String initialText,
        Consumer<OfflinePlayer> callback,
        Runnable cancelCallback
    ) {
        new InputScreen(
            player,
            Message.PLAYER_NAME_INPUT.asComponent(NamedTextColor.WHITE),
            initialText,
            name -> {
                if (name.isEmpty()) {
                    return;
                }
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                if (!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) {
                    player.sendMessage(Message.PLAYER_NOT_FOUND.asComponent(NamedTextColor.RED, name));
                    return;
                }
                callback.accept(offlinePlayer);
            },
            cancelCallback
        ).open();
    }

    private final NamespacedKey anvilInputItemKey = new NamespacedKey(SchneckenhausPlugin.INSTANCE, "anvil_input_item");

    private final Player player;
    private final Component title;
    private final String initialText;
    private final Consumer<String> callback;
    private final Runnable cancelCallback;

    private AnvilInventory inventory;
    private AnvilView view;

    public InputScreen(
        final Player player,
        final Component title,
        final String initialText,
        final Consumer<String> callback,
        final Runnable cancelCallback
    ) {
        this.player = player;
        this.title = title;
        this.initialText = initialText;
        this.callback = callback;
        this.cancelCallback = cancelCallback != null ? cancelCallback : () -> {};
    }

    public void open() {
        view = MenuType.ANVIL.builder()
            .title(title)
            .checkReachable(false)
            .build(player);
        view.open();
        inventory = view.getTopInventory();

        ItemStack firstItem = new ItemStack(Material.PAPER);
        firstItem.editMeta(meta -> {
            meta.customName(Component.text(initialText));
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(anvilInputItemKey, PersistentDataType.BOOLEAN, true);
        });
        inventory.setFirstItem(firstItem);

        Bukkit.getPluginManager().registerEvents(this, SchneckenhausPlugin.INSTANCE);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(player) || !event.getInventory().equals(inventory)) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().isMouseClick() || event.getSlot() != 2) {
            return;
        }
        String input = view.getRenameText();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            SchneckenhausPlugin.INSTANCE, () -> {
            player.closeInventory();
            callback.accept(input);

            removePaperFromInventory();
        });
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(SchneckenhausPlugin.INSTANCE, cancelCallback);
        Bukkit.getScheduler().runTask(SchneckenhausPlugin.INSTANCE, this::removePaperFromInventory);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        HandlerList.unregisterAll(this);
        cancelCallback.run();
    }

    private void removePaperFromInventory() {
        // Make sure that the rename item doesn't end up in the player's inventory
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            if (item.getItemMeta().getPersistentDataContainer().has(anvilInputItemKey)) {
                player.getInventory().setItem(i, null);
            }
        }
    }
}