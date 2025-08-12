package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AndShellConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "and";

    private List<ShellConditionConfig> conditions;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return conditions.stream().allMatch(condition -> condition.check(data));
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (!(config.get("conditions") instanceof List<?> serializedConditions)) {
            return false;
        }
        conditions = new ArrayList<>();
        for (Object serializedCondition : serializedConditions) {
            ShellConditionConfig condition = ShellConditionConfigFactory.deserialize(serializedCondition);
            if (condition != null) {
                conditions.add(condition);
            }
        }
        return !conditions.isEmpty();
    }

    @Override
    protected void serialize(Map<String, Object> result) {
        result.put(
            "conditions",
            conditions.stream().map(ShellConditionConfig::serialize).toList()
        );
    }
}
