package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.config.condition.ShellConditionConfig;
import io.github.lama06.schneckenhaus.config.condition.ShellConditionConfigFactory;

import java.util.*;

public final class WorldConfig {
    private boolean fallback;
    private List<ShellConditionConfig> conditions = new ArrayList<>();

    public WorldConfig() { }

    public WorldConfig(boolean fallback, List<ShellConditionConfig> conditions) {
        this.fallback = fallback;
        this.conditions = conditions;
    }

    public void deserialize(Map<?, ?> config) {
        if (config.get("fallback") instanceof Boolean fallback) {
            this.fallback = fallback;
        }
        if (config.get("conditions") instanceof List<?> conditions) {
            this.conditions = conditions.stream()
                .map(ShellConditionConfigFactory::deserialize)
                .filter(Objects::nonNull)
                .toList();
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("fallback", fallback);
        config.put("conditions", conditions.stream().map(ShellConditionConfig::serialize).toList());
        return config;
    }

    public boolean isFallback() {
        return fallback;
    }

    public List<ShellConditionConfig> getConditions() {
        return conditions;
    }
}
