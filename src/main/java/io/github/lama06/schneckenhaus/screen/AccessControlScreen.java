package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.shell.AccessMode;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
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
import java.util.UUID;

public final class AccessControlScreen extends Screen {
    private final Shell<?> shell;

    public AccessControlScreen(Player player, Shell<?> shell) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Component.text("Access Control");
    }

    @Override
    protected int getHeight() {
        return 6;
    }

    @Override
    protected void draw() {
        for (int x = 0; x < 9; x++) {
            setItem(x, 0, InventoryUtil.createMarginItem());
        }

        AccessMode mode = Shell.ACCESS_MODE.get(shell);
        ItemStack modeItem = new ItemStack(mode.icon);
        modeItem.editMeta(meta -> {
            meta.customName(Component.text("Mode: ").append(mode.name));
            ArrayList<Component> lore = new ArrayList<>(mode.description);
            lore.add(Component.text("Click to change", NamedTextColor.YELLOW));
            meta.lore(lore);
        });
        setItem(4, 0, modeItem, () -> {
            Shell.ACCESS_MODE.set(shell, EnumUtil.getNext(mode));
            redraw();
        });

        if (mode != AccessMode.WHITELIST && mode != AccessMode.BLACKLIST) {
            return;
        }

        Attribute<List<UUID>> attribute = mode == AccessMode.WHITELIST ? Shell.WHITELIST : Shell.BLACKLIST;
        List<UUID> list = new ArrayList<>(attribute.getOrDefault(shell, List.of()));

        ItemStack addPlayerItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        addPlayerItem.editMeta(meta -> {
            String addPlayerLabel = "Add Player to " + (mode == AccessMode.WHITELIST ? "Whitelist" : "Blacklist");
            meta.customName(Component.text(addPlayerLabel, NamedTextColor.GREEN));
        });
        setItem(8, 0, addPlayerItem, () -> InputScreen.openPlayerNameInput(
            player, "", addedPlayer -> {
                if (!list.contains(addedPlayer.getUniqueId())) {
                    list.add(addedPlayer.getUniqueId());
                    attribute.set(shell, list);
                }
                player.sendMessage(Component.text(addedPlayer.getName() + " was added", NamedTextColor.GREEN));
                new AccessControlScreen(player, shell).open();
            }
        ));

        for (int i = 0; i < list.size(); i++) {
            OfflinePlayer listPlayer = Bukkit.getOfflinePlayer(list.get(i));

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            head.editMeta(SkullMeta.class, meta -> {
                meta.customName(Component.text(listPlayer.getName()));
                meta.lore(List.of(
                    Component.text("Click to remove", NamedTextColor.RED)
                ));
                meta.setOwningPlayer(listPlayer);
            });
            setItem(9 + i, head, () -> {
                list.remove(listPlayer.getUniqueId());
                attribute.set(shell, list);
                redraw();
            });
        }
    }
}
