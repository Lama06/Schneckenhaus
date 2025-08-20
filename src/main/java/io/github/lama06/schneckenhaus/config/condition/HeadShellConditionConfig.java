package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.head.HeadShellData;

import java.util.Map;

public final class HeadShellConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "head";

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return data instanceof HeadShellData;
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        return true;
    }

    @Override
    protected void serialize(Map<String, Object> result) { }
}
