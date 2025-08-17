package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.bukkit.event.Listener;

public abstract class System extends ConstantsHolder implements Listener {
    public boolean isEnabled() {
        return true;
    }

    public void start() { }
}
