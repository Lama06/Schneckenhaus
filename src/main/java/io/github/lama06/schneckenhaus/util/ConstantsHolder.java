package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.config.SchneckenhausConfig;
import org.slf4j.Logger;

import java.sql.Connection;

public abstract class ConstantsHolder {
    protected final SchneckenhausPlugin plugin = SchneckenhausPlugin.INSTANCE;
    protected final Connection connection = plugin.getDBConnection();
    protected final Logger logger = plugin.getSLF4JLogger();
    protected final SchneckenhausConfig config = plugin.getPluginConfig();
}
