package io.github.lama06.schneckenhaus.shell;

import org.bukkit.inventory.ItemStack;

public abstract class ShellMenuAction {

    protected ShellMenuAction() { }

    public abstract ItemStack getItem();

    public Integer getItemAnimationDelay() {
        return null;
    }

    public abstract void onClick();
}
