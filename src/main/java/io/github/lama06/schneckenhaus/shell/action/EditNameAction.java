package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.ui.InputScreen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EditNameAction extends ShellScreenAction {
    public EditNameAction(Shell shell, Player player) {
        super(shell, player);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        item.editMeta(meta -> {
            if (shell.getName() == null) {
                meta.customName(Message.NAME_NOT_SET.asComponent());
            } else {
                meta.customName(Message.SNAIL_SHELL_NAME.asComponent(shell.getName()));
            }
            if (Permission.RENAME_SNAIL_SHELL.check(player)) {
                meta.lore(List.of(Message.CLICK_TO_CHANGE_NAME.asComponent(NamedTextColor.YELLOW)));
            }
        });
        return item;
    }

    @Override
    public void onClick() {
        if (!Permission.RENAME_SNAIL_SHELL.check(player)) {
            return;
        }
        new InputScreen(
            player,
            Message.RENAME_SHELL_TITLE.asComponent(NamedTextColor.YELLOW),
            shell.getName() == null ? "" : shell.getName(),
            newName -> {
                shell.setName(newName);
                player.sendMessage(Message.RENAME_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN, newName));
            },
            () -> {
            }
        ).open();
    }
}
