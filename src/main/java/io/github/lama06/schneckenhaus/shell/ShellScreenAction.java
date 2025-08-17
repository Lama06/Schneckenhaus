package io.github.lama06.schneckenhaus.shell;

import org.bukkit.inventory.ItemStack;

public abstract class ShellScreenAction {
    protected ShellScreenAction() { }

    public abstract ItemStack getItem();

    public Integer getItemAnimationDelay() {
        return null;
    }

    public abstract void onClick();

    public boolean isAlignedRight() {
        return false;
    }
}
