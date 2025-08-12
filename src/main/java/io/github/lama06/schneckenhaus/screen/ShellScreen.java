package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellMenuAction;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShellScreen extends Screen {
    private final Shell shell;

    public ShellScreen(Shell shell, Player player) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Message.SNAIL_SHELL.toComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 3;
    }

    @Override
    protected void draw() {
        InventoryUtil.fillMargin(inventory);

        Integer smallestAnimationDelay = null;
        int slot = 10;
        for (ShellMenuAction action : shell.getShellMenuActions(player)) {
            ItemStack item = action.getItem();
            if (item == null) {
                continue;
            }
            setItem(slot++, item, action::onClick);
            Integer delay = action.getItemAnimationDelay();
            if (delay != null) {
                if (smallestAnimationDelay == null) {
                    smallestAnimationDelay = delay;
                } else {
                    smallestAnimationDelay = Math.min(smallestAnimationDelay, delay);
                }
            }
        }

        if (smallestAnimationDelay == null) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(
            SchneckenPlugin.INSTANCE,
            () -> {
                if (isOpen()) {
                    redraw();
                }
            },
            smallestAnimationDelay
        );
    }
}
