package io.github.lama06.schneckenhaus.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConditionalTaskFeatureConfig extends ConditionalFeatureConfig {
    private int delay;

    public ConditionalTaskFeatureConfig(int delay) {
        this.delay = delay;
    }

    @Override
    public void deserialize(Map<?, ?> config) {
        super.deserialize(config);
        if (config.get("delay") instanceof Integer delay) {
            this.delay = delay;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> config = new LinkedHashMap<>(super.serialize());
        config.put("delay", delay);
        return config;
    }

    public int getDelay() {
        return delay;
    }
}
