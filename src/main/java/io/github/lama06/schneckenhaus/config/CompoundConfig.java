package io.github.lama06.schneckenhaus.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CompoundConfig {
    protected abstract List<ConfigAttribute<?>> getAttributes();

    public final void load(final Map<String, Object> data) throws ConfigException {
        for (final ConfigAttribute<?> attribute : getAttributes()) {
            attribute.load(data);
        }
    }

    public final void verify() throws ConfigException {
        for (final ConfigAttribute<?> attribute : getAttributes()) {
            attribute.verify();
        }
    }

    public final Map<String, Object> store() {
        final Map<String, Object> data = new LinkedHashMap<>();
        for (final ConfigAttribute<?> attribute : getAttributes()) {
            attribute.store(data);
        }
        return data;
    }
}
