package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.action.ShellScreenAction;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

public class ShellScreen extends Screen {
    private final Shell shell;
    private final List<ShellScreenAction> actions;

    public ShellScreen(Shell shell, Player player) {
        super(player);
        this.shell = shell;
        actions = shell.getShellScreenActions(player)
            .stream()
            .sorted(Comparator.comparingInt(action -> action.isAlignedRight() ? 1 : 0))
            .toList();
    }

    @Override
    protected Component getTitle() {
        return Message.SNAIL_SHELL.asComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        if (actions.size() >= 8) {
            return 4;
        }
        return 3;
    }

    @Override
    protected void draw() {
        InventoryUtil.fillMargin(inventory);

        int slot = 10;
        for (ShellScreenAction action : actions) {
            if (action.getItem() == null) {
                continue;
            }
            if (slot == 17) {
                slot += 2;
            }
            setItem(
                InventoryPosition.fromSlot(slot++),
                action::getItem,
                action.getItemAnimationDelay(),
                () -> {
                    action.onClick();
                    redraw();
                }
            );
        }
    }
}
