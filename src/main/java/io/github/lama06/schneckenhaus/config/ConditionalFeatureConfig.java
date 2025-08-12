package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.config.condition.ShellConditionConfig;
import io.github.lama06.schneckenhaus.config.condition.ShellConditionConfigFactory;
import io.github.lama06.schneckenhaus.shell.ShellData;

import java.util.*;

public class ConditionalFeatureConfig {
    private boolean enabled = true;
    private List<ShellConditionConfig> conditions = List.of();

    public boolean check(ShellData data) {
        if (conditions.isEmpty()) {
            return enabled;
        }
        return enabled && conditions.stream().anyMatch(condition -> condition.check(data));
    }

    public void deserialize(Map<?, ?> config) {
        if (config.get("enabled") instanceof Boolean enabled) {
            this.enabled = enabled;
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
        config.put("enabled", enabled);
        config.put("conditions", conditions.stream().map(ShellConditionConfig::serialize).toList());
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
