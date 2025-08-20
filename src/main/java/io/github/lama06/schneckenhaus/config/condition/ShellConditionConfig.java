package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ShellConditionConfig extends ConstantsHolder {
    protected abstract String getType();

    public abstract boolean check(ShellData data);

    public abstract boolean deserialize(Map<?, ?> config);

    protected abstract void serialize(Map<String, Object> result);

    public final Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", getType());
        serialize(result);
        return result;
    }
}
