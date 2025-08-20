package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.action.ShellScreenAction;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

class ChangeWoodAction extends ShellScreenAction {
    private final ChestShell shell;

    public ChangeWoodAction(ChestShell shell, Player player) {
        super(shell, player);
        this.shell = shell;
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.CHANGE_SHELL_WOOD.check(player)) {
            return null;
        }

        ItemStack item = new ItemStack(shell.getWood().getSapling());
        item.editMeta(meta -> {
            meta.customName(Message.WOOD.asComponent());
            meta.lore(List.of(Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)));
        });
        return item;
    }

    @Override
    public void onClick() {
        new ChestShellWoodScreen(shell, player).open();
    }
}
