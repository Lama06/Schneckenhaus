package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellData;

import java.util.Map;

public final class CustomShellConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "custom";

    private String template;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return data instanceof CustomShellData customData &&
            (template == null || template.equals(customData.getTemplate()));
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (config.get("template") instanceof String template) {
            this.template = template;
        }
        return true;
    }

    @Override
    protected void serialize(Map<String, Object> result) {
        result.put("template", template == null ? null : template);
    }
}
