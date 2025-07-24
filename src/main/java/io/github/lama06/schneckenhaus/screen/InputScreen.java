package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.data.Attribute;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public final class InputScreen implements Listener {
    public static void open(
        final Player player,
        final Component title,
        final String initialText,
        final Consumer<String> callback,
        final Runnable cancelCallback
    ) {
        if (!player.isConnected()) {
            return;
        }

        new InputScreen(player, title, initialText, callback, cancelCallback).open();
    }

    public static void openPlayerNameInput(
        final Player player,
        final String initialText,
        final Consumer<OfflinePlayer> callback
    ) {
        open(
            player,
            Component.text("Type Player Name...", NamedTextColor.WHITE),
            initialText,
            name -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                if (!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) {
                    player.sendMessage(Component.text("Player not found on this server: " + name, NamedTextColor.RED));
                    return;
                }
                callback.accept(offlinePlayer);
            },
            () -> {}
        );
    }

    private static final Attribute<Boolean> ANVIL_INPUT_ITEM = new Attribute<>("anvil_input_item", PersistentDataType.BOOLEAN);

    private final Player player;
    private final Component title;
    private final String initialText;
    private final Consumer<String> callback;
    private final Runnable cancelCallback;

    private AnvilInventory inventory;
    private AnvilView view;

    private InputScreen(
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

    private void open() {
        view = MenuType.ANVIL.builder()
            .title(title)
            .checkReachable(false)
            .build(player);
        view.open();
        inventory = view.getTopInventory();
        final ItemStack firstItem = new ItemStack(Material.PAPER);
        final ItemMeta firstItemMeta = firstItem.getItemMeta();
        firstItemMeta.customName(Component.text(initialText));
        ANVIL_INPUT_ITEM.set(firstItemMeta, true);
        firstItem.setItemMeta(firstItemMeta);
        inventory.setFirstItem(firstItem);

        Bukkit.getPluginManager().registerEvents(this, SchneckenPlugin.INSTANCE);
    }

    @EventHandler
    private void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(player) || !event.getInventory().equals(inventory)) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().isMouseClick() || event.getSlot() != 2) {
            return;
        }
        String input = view.getRenameText();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(SchneckenPlugin.INSTANCE, () -> {
            player.closeInventory();
            callback.accept(input);

            // Make sure that the rename item doesn't end up in the player's inventory
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) {
                    continue;
                }
                if (ANVIL_INPUT_ITEM.has(item.getItemMeta())) {
                    player.getInventory().setItem(i, null);
                }
            }
        });
    }

    @EventHandler
    private void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        Bukkit.getScheduler().runTask(SchneckenPlugin.INSTANCE, cancelCallback);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onPlayerQuit(final PlayerQuitEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }
        cancelCallback.run();
        HandlerList.unregisterAll(this);
    }
}