package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;

import java.util.Map;

public final class NotShellConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "not";

    private ShellConditionConfig condition;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return !condition.check(data);
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        condition = ShellConditionConfigFactory.deserialize(config.get("condition"));
        return condition != null;
    }

    @Override
    protected void serialize(Map<String, Object> result) {
        result.put("condition", condition.serialize());
    }
}
