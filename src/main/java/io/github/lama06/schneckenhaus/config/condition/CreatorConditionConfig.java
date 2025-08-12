package io.github.lama06.schneckenhaus.config.condition;

import io.github.lama06.schneckenhaus.shell.ShellCreationType;
import io.github.lama06.schneckenhaus.shell.ShellData;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class CreatorConditionConfig extends ShellConditionConfig {
    public static final String TYPE = "creation";

    private ShellCreationType creationType;
    private UUID creator;
    private String permission;

    @Override
    protected String getType() {
        return TYPE;
    }

    @Override
    public boolean check(ShellData data) {
        return (creationType == null || data.getCreationType() == creationType) &&
            (creator == null || creator.equals(data.getCreator())) &&
            (permission == null ||
                (Bukkit.getPlayer(creator) != null && Bukkit.getPlayer(creator).hasPermission(permission)));
    }

    @Override
    public boolean deserialize(Map<?, ?> config) {
        if (config.get("creation_type") instanceof String creationTypeName) {
            try {
                creationType = ShellCreationType.valueOf(creationTypeName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) { }
        }
        if (config.get("creator") instanceof String creatorString) {
            try {
                creator = UUID.fromString(creatorString);
            } catch (IllegalArgumentException ignored) { }
        }
        if (config.get("permission") instanceof String permission) {
            this.permission = permission;
        }
        return true;
    }

    @Override
    protected void serialize(Map<String, Object> result) {
        if (creationType != null) {
            result.put("creation_type", creationType.name().toLowerCase(Locale.ROOT));
        }
        if (creator != null) {
            result.put("creator", creator.toString());
        }
        if (permission != null) {
            result.put("permission", permission);
        }
    }
}
