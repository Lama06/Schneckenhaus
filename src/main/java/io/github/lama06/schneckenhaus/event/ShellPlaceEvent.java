package io.github.lama06.schneckenhaus.event;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

public final class ShellPlaceEvent extends ShellEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final Block block;

    public ShellPlaceEvent(Shell shell, Block block) {
        super(shell);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
