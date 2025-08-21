package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.ui.ConfirmationScreen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class DeleteAction extends ShellScreenAction {
    public DeleteAction(Shell shell, Player player) {
        super(shell, player);
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.DELETE_SHELL.check(player)) {
            return null;
        }
        ItemStack item = new ItemStack(Material.TNT);
        item.editMeta(meta -> {
            meta.customName(Message.DELETE.asComponent(NamedTextColor.RED));
        });
        return item;
    }

    @Override
    public void onClick() {
        new ConfirmationScreen(
            player,
            Message.DELETE.toString(),
            () -> {
            },
            () -> {
                shell.delete();
                player.sendMessage(Message.DELETE_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN, 1));
            }
        ).open();
    }

    @Override
    public boolean isAlignedRight() {
        return true;
    }
}
