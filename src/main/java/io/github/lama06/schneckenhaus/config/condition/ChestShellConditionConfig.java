package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.chest.ChestShellData;

public final class ChestShellConditionConfig extends SizedShellConditionConfig {
    public static final String TYPE = "chest";

    @Override
    public boolean check(ShellData data) {
        return data instanceof ChestShellData;
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
