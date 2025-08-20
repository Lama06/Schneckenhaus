package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.chest.ChestShellData;
import io.github.lama06.schneckenhaus.util.WoodType;

import java.util.Locale;
import java.util.Map;

public final class ChestShellConditionConfig extends SizedShellConditionConfig {
    public static final String TYPE = "chest";

    private WoodType wood;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return super.check(data) && data instanceof ChestShellData chestData && (wood == null || wood.equals(chestData.getWood()));
    }

    @Override
    public void serialize(Map<String, Object> result) {
        super.serialize(result);
        result.put("wood", wood == null ? null : wood.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (!super.deserialize(config)) {
            return false;
        }
        if (config.get("wood") instanceof String wood) {
            try {
                this.wood = WoodType.valueOf(wood.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) { }
        }
        return true;
    }
}
