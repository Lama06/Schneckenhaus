package io.github.lama06.schneckenhaus.event;

import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.event.Event;

public abstract class ShellEvent extends Event {
    private final Shell shell;

    protected ShellEvent(Shell shell) {
        this.shell = shell;
    }

    public Shell getShell() {
        return shell;
    }
}
