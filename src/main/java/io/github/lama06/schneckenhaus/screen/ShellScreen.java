package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

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

    }
}
