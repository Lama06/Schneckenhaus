package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellData;
import io.github.lama06.schneckenhaus.util.IntegerRange;

import java.util.Map;

public abstract class SizedShellConditionConfig extends ShellConditionConfig {
    private IntegerRange size;

    @Override
    public boolean check(ShellData data) {
        return data instanceof SizedShellData sizedData && (size == null || size.contains(sizedData.getSize()));
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        size = IntegerRange.deserialize(config.get("size"));
        return true;
    }

    @Override
    public void serialize(Map<String, Object> result) {
        result.put("size", size == null ? null : size.serialize());
    }
}
