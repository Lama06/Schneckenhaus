package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.Comparator;

public record PluginVersion(int major, int minor, int patch) implements Comparable<PluginVersion> {
    public static final PersistentDataType<String, PluginVersion> DATA_TYPE = new PersistentDataType<>() {
        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<PluginVersion> getComplexType() {
            return PluginVersion.class;
        }

        @Override
        public String toPrimitive(final PluginVersion pluginVersion, final PersistentDataAdapterContext context) {
            return pluginVersion.toString();
        }

        @Override
        public PluginVersion fromPrimitive(final String string, final PersistentDataAdapterContext context) {
            return fromString(string);
        }
    };

    public static PluginVersion current() {
        return fromString(SchneckenPlugin.INSTANCE.getDescription().getVersion());
    }

    public static PluginVersion fromString(final String string) {
        final String[] versionParts = string.split("\\.");
        if (versionParts.length != 3) {
            return null;
        }
        final int major, minor, patch;
        try {
            major = Integer.parseInt(versionParts[0]);
            minor = Integer.parseInt(versionParts[1]);
            patch = Integer.parseInt(versionParts[2]);
        } catch (final NumberFormatException e) {
            return null;
        }
        return new PluginVersion(major, minor, patch);
    }

    public PluginVersion {
        if (major <= 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(final PluginVersion other) {
        final Comparator<PluginVersion> comparator = Comparator.comparingInt(PluginVersion::major)
                .thenComparingInt(PluginVersion::minor)
                .thenComparingInt(PluginVersion::patch);
        return comparator.compare(this, other);
    }

    @Override
    public String toString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }
}
