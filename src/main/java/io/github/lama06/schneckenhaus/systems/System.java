package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import org.bukkit.event.Listener;
import org.slf4j.Logger;

import java.sql.Connection;

public abstract class System implements Listener {
    protected final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
    protected final SchneckenhausConfig config = plugin.getPluginConfig();
    protected final Connection connection = plugin.getDBConnection();
    protected final Logger logger = plugin.getSLF4JLogger();

    public boolean isEnabled() {
        return true;
    }

    public void start() { }
}
