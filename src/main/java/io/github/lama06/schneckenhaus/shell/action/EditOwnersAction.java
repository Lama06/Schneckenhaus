package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.ui.PlayerListEditScreen;
import io.github.lama06.schneckenhaus.ui.ShellScreen;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class EditOwnersAction extends ShellScreenAction {
    private static final int ANIMATION_DELAY = 20;

    private final List<UUID> ownerUuids;

    public EditOwnersAction(Shell shell, Player player) {
        super(shell, player);
        ownerUuids = shell.getOwners().get().stream().sorted().toList();
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.EDIT_OWNERS.check(player)) {
            return null;
        }

        if (ownerUuids.isEmpty()) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            item.editMeta(meta -> {
                meta.customName(Message.OWNERS.asComponent());
                meta.lore(List.of(
                    Message.EMPTY.asComponent(NamedTextColor.RED),
                    Message.CLICK_TO_ADD.asComponent(NamedTextColor.YELLOW)
                ));
            });
            return item;
        }

        int currentlyDisplayOwnerIndex = (Bukkit.getCurrentTick() / ANIMATION_DELAY) % ownerUuids.size();
        UUID currentlyDisplayedOwnerUuid = ownerUuids.get(currentlyDisplayOwnerIndex);
        OfflinePlayer currentlyDisplayedOwner = Bukkit.getOfflinePlayer(currentlyDisplayedOwnerUuid);
        String currentlyDisplayedOwnerName = Objects.requireNonNullElse(
            currentlyDisplayedOwner.getName(),
            currentlyDisplayedOwner.getUniqueId().toString()
        );

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.editMeta(
            SkullMeta.class, meta -> {
                meta.customName(Message.OWNERS.asComponent(NamedTextColor.YELLOW));
                meta.lore(List.of(
                    Component.text(currentlyDisplayedOwnerName),
                    Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)
                ));
                meta.setOwningPlayer(currentlyDisplayedOwner);
            }
        );
        return head;
    }

    @Override
    public Integer getItemAnimationDelay() {
        if (ownerUuids.size() <= 1) {
            return null;
        }
        return ANIMATION_DELAY;
    }

    @Override
    public void onClick() {
        new PlayerListEditScreen(
            player,
            Message.OWNERS.asComponent(NamedTextColor.YELLOW),
            shell.getOwners().get(),
            shell.getOwners()::set,
            () -> new ShellScreen(shell, player).open()
        ).open();
    }
}
