package io.github.lama06.schneckenhaus.screen;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class PlayerListEditScreen extends Screen {
    private final Component title;
    private final List<UUID> list;
    private final Consumer<List<UUID>> update;
    private final Runnable callback;

    public PlayerListEditScreen(
        Player player,
        Component title,
        List<UUID> list,
        Consumer<List<UUID>> update,
        Runnable callback
    ) {
        super(player);
        this.title = title;
        this.list = new ArrayList<>(list);
        this.update = update;
        this.callback = callback;
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
            meta.customName(Component.text(t("ui_player_list_add"), NamedTextColor.GREEN));
        });
        setItem(0, 0, addPlayerItem, () -> InputScreen.openPlayerNameInput(
            player, "", addedPlayer -> {
                if (!list.contains(addedPlayer.getUniqueId())) {
                    list.add(addedPlayer.getUniqueId());
                    update.accept(list);
                }
                player.sendMessage(Component.text(t("ui_player_list_add_success", addedPlayer.getName()), NamedTextColor.GREEN));
                new PlayerListEditScreen(player, title, list, update, callback).open();
            }
        ));

        ItemStack back = new ItemStack(Material.ARROW);
        back.editMeta(meta -> {
            meta.customName(Component.text(t("ui_player_list_back"), NamedTextColor.GREEN));
        });
        setItem(8, 0, back, callback);

        for (int i = 0; i < list.size(); i++) {
            OfflinePlayer listPlayer = Bukkit.getOfflinePlayer(list.get(i));

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            head.editMeta(SkullMeta.class, meta -> {
                meta.customName(Component.text(Objects.requireNonNullElse(
                    listPlayer.getName(),
                    listPlayer.getUniqueId().toString()
                )));
                meta.lore(List.of(
                    Component.text(t("ui_player_list_remove"), NamedTextColor.RED)
                ));
                meta.setOwningPlayer(listPlayer);
            });
            setItem(9 + i, head, () -> {
                list.remove(listPlayer.getUniqueId());
                update.accept(list);
                redraw();
            });
        }
    }
}
