package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.action.ShellScreenAction;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class ChangeColorAction extends ShellScreenAction {
    private final ShulkerShell shell;

    ChangeColorAction(ShulkerShell shell, Player player) {
        super(shell, player);
        this.shell = shell;
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.CHANGE_SNAIL_SHELL_COLOR.check(player)) {
            return null;
        }
        ItemStack item = new ItemStack(MaterialUtil.getColoredDye(shell.getCurrentColor()));
        item.editMeta(meta -> {
            meta.customName(Message.COLOR.asComponent(TextColor.color(shell.getCurrentColor().getColor().asRGB())));
            meta.lore(List.of(Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)));
        });
        return item;
    }

    @Override
    public Integer getItemAnimationDelay() {
        return shell.getFactory().getItemAnimationDelay(shell);
    }

    @Override
    public void onClick() {
        new ShulkerColorScreen(player, shell).open();
    }
}
