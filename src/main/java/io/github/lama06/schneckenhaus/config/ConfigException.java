package io.github.lama06.schneckenhaus.config;

public final class ConfigException extends Exception {
    private final ConfigPath path;

    public ConfigException(final String message, final ConfigPath.Component... path) {
        super(message);
        this.path = new ConfigPath(path);
    }

    public ConfigException(final String message, final String path) {
        this(message, new ConfigPath.Name(path));
    }

    public ConfigPath getPath() {
        return path;
    }
}
