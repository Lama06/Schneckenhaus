package io.github.lama06.schneckenhaus.config.condition;

import java.util.Map;
import java.util.function.Supplier;

public class ShellConditionConfigFactory {
    private static final Map<String, Supplier<ShellConditionConfig>> CONSTRUCTORS = Map.ofEntries(
        Map.entry(AndShellConditionConfig.TYPE, AndShellConditionConfig::new),
        Map.entry(ChestShellConditionConfig.TYPE, ChestShellConditionConfig::new),
        Map.entry(CreationConditionConfig.TYPE, CreationConditionConfig::new),
        Map.entry(CustomShellConditionConfig.TYPE, CustomShellConditionConfig::new),
        Map.entry(HeadShellConditionConfig.TYPE, HeadShellConditionConfig::new),
        Map.entry(NotShellConditionConfig.TYPE, NotShellConditionConfig::new),
        Map.entry(ShulkerShellConditionConfig.TYPE, ShulkerShellConditionConfig::new),
        Map.entry(TagShellConditionConfig.TYPE, TagShellConditionConfig::new)
    );

    public static ShellConditionConfig deserialize(Object config) {
        if (!(config instanceof Map<?, ?> map)) {
            return null;
        }
        if (!(map.get("type") instanceof String type)) {
            return null;
        }
        Supplier<ShellConditionConfig> constructor = CONSTRUCTORS.get(type);
        if (constructor == null) {
            return null;
        }
        ShellConditionConfig condition = constructor.get();
        if (!condition.deserialize(map)) {
            return null;
        }
        return condition;
    }
}
