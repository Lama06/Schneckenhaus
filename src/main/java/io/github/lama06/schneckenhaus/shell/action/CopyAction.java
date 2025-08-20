package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CopyAction extends ShellScreenAction {
    public CopyAction(Shell shell, Player player) {
        super(shell, player);
    }

    @Override
    public ItemStack getItem() {
        // item without id to prevent the animation system from messing with it
        ItemStack item = shell.createItem(false);
        item.editMeta(meta -> {
            List<Component> lore = new ArrayList<>(meta.hasLore() ? meta.lore() : List.of());
            if (Permission.CREATE_SNAIL_SHELL_COPIES.check(player)) {
                lore.add(Message.CLICK_TO_CREATE_SHELL_COPY.asComponent(NamedTextColor.YELLOW));
            }
            meta.lore(lore);
        });
        return item;
    }

    @Override
    public Integer getItemAnimationDelay() {
        return shell.getFactory().getItemAnimationDelay(shell);
    }

    @Override
    public void onClick() {
        if (!Permission.CREATE_SNAIL_SHELL_COPIES.check(player)) {
            return;
        }
        player.give(shell.createItem());
    }
}
