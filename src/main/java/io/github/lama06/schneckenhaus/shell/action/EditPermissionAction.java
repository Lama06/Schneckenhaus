package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermission;
import io.github.lama06.schneckenhaus.ui.PermissionScreen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EditPermissionAction extends ShellScreenAction {
    private final ShellPermission permission;
    private final Permission editPermission;
    private final Material icon;
    private final Message name;

    public EditPermissionAction(
        Shell shell,
        Player player,
        ShellPermission permission,
        Permission editPermission,
        Material icon,
        Message name
    ) {
        super(shell, player);
        this.permission = permission;
        this.editPermission = editPermission;
        this.icon = icon;
        this.name = name;
    }

    @Override
    public ItemStack getItem() {
        if (!editPermission.check(player)) {
            return null;
        }
        ItemStack item = new ItemStack(icon);
        item.editMeta(meta -> {
            meta.customName(name.asComponent(NamedTextColor.YELLOW));
            meta.lore(List.of(Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)));
        });
        return item;
    }

    @Override
    public void onClick() {
        new PermissionScreen(player, permission).open();
    }
}
