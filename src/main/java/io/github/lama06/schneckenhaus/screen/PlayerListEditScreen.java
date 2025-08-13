package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.language.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.BiConsumer;

public final class PlayerListEditScreen extends Screen {
    private final Component title;
    private final List<UUID> players;
    private final BiConsumer<UUID, Boolean> callback;
    private final Runnable closeCallback;

    public PlayerListEditScreen(
        Player player,
        Component title,
        Collection<UUID> players,
        BiConsumer<UUID, Boolean> callback,
        Runnable closeCallback
    ) {
        super(player);
        this.title = title;
        this.players = new ArrayList<>(players);
        this.callback = callback;
        this.closeCallback = closeCallback;
    }

    @Override
    protected Component getTitle() {
        return title;
    }

    @Override
    protected int getHeight() {
        return 6;
    }

    @Override
    protected void draw() {
        ItemStack addPlayerItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        addPlayerItem.editMeta(meta -> {
            meta.customName(Message.ADD_PLAYER.asComponent(NamedTextColor.GREEN));
        });
        setItem(0, 0, addPlayerItem, () -> InputScreen.openPlayerNameInput(
            player, "", addedPlayer -> {
                if (!players.contains(addedPlayer.getUniqueId())) {
                    players.add(addedPlayer.getUniqueId());
                    callback.accept(addedPlayer.getUniqueId(), true);
                }
                player.sendMessage(Message.ADD_PLAYER_SUCCESS.asComponent(NamedTextColor.GREEN, addedPlayer.getName()));
                new PlayerListEditScreen(player, title, players, callback, closeCallback).open();
            },
            () -> new PlayerListEditScreen(player, title, players, callback, closeCallback).open()
        ));

        ItemStack back = new ItemStack(Material.ARROW);
        back.editMeta(meta -> {
            meta.customName(Message.BACK.asComponent(NamedTextColor.GREEN));
        });
        setItem(8, 0, back, closeCallback);

        for (int i = 0; i < players.size(); i++) {
            OfflinePlayer listPlayer = Bukkit.getOfflinePlayer(players.get(i));

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            head.editMeta(SkullMeta.class, meta -> {
                meta.customName(Component.text(Objects.requireNonNullElse(
                    listPlayer.getName(),
                    listPlayer.getUniqueId().toString()
                )));
                meta.lore(List.of(Message.CLICK_TO_REMOVE.asComponent(NamedTextColor.YELLOW)));
                meta.setOwningPlayer(listPlayer);
            });
            setItem(9 + i, head, () -> {
                players.remove(listPlayer.getUniqueId());
                callback.accept(listPlayer.getUniqueId(), false);
                redraw();
            });
        }
    }
}
