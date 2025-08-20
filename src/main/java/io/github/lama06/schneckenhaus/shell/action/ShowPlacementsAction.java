package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.ui.ShellPlacementsScreen;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ShowPlacementsAction extends ShellScreenAction {
    public ShowPlacementsAction(Shell shell, Player player) {
        super(shell, player);
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.PLACEMENTS_VIEW.check(player)) {
            return null;
        }
        ItemStack item = new ItemStack(Material.COMPASS);
        item.editMeta(meta -> {
            meta.customName(Message.SHELL_PLACEMENTS.asComponent());
            meta.lore(List.of(Message.CLICK_TO_OPEN.asComponent(NamedTextColor.YELLOW)));
        });
        return item;
    }

    @Override
    public void onClick() {
        new ShellPlacementsScreen(player, shell).open();
    }
}
