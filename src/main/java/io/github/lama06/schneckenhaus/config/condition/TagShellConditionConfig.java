package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellData;

import java.util.Map;

public final class TagShellConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "tag";

    private String tag;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return data instanceof Shell shell && shell.hasTag(tag);
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (!(config.get("tag") instanceof String tag)) {
            return false;
        }
        this.tag = tag;
        return true;
    }

    @Override
    protected void serialize(Map<String, Object> result) {
        result.put("tag", tag);
    }
}
