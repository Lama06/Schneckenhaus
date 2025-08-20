package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;

import java.util.Comparator;

public record PluginVersion(int major, int minor, int patch) implements Comparable<PluginVersion> {
    public static PluginVersion current() {
        return fromString(SchneckenhausPlugin.INSTANCE.getPluginMeta().getVersion());
    }

    public static PluginVersion fromString(String string) {
        final String[] versionParts = string.split("\\.");
        if (versionParts.length != 3) {
            return null;
        }
        int major, minor, patch;
        try {
            major = Integer.parseInt(versionParts[0]);
            minor = Integer.parseInt(versionParts[1]);
            patch = Integer.parseInt(versionParts[2]);
        } catch (NumberFormatException e) {
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
    public int compareTo(PluginVersion other) {
        Comparator<PluginVersion> comparator = Comparator.comparingInt(PluginVersion::major)
                .thenComparingInt(PluginVersion::minor)
                .thenComparingInt(PluginVersion::patch);
        return comparator.compare(this, other);
    }

    @Override
    public String toString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }
}
