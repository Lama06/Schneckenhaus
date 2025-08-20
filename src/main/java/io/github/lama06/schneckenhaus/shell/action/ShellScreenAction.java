package io.github.lama06.schneckenhaus.shell.action;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ShellScreenAction {
    protected final Shell shell;
    protected final Player player;

    protected ShellScreenAction(Shell shell, Player player) {
        this.shell = shell;
        this.player = player;
    }

    public abstract ItemStack getItem();

    public Integer getItemAnimationDelay() {
        return null;
    }

    public abstract void onClick();

    public boolean isAlignedRight() {
        return false;
    }
}
