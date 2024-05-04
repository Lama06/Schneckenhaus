package io.github.lama06.schneckenhaus.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConfigPath {
    private final List<Component> components;

    public ConfigPath(final Component... components) {
        this.components = new ArrayList<>(Arrays.asList(components));
    }

    public void addPrefix(final Component prefix) {
        components.add(0, prefix);
    }

    public void addPrefix(final String prefix) {
        addPrefix(new Name(prefix));
    }

    public void addPrefix(final int index) {
        addPrefix(new Index(index));
    }

    public void addSuffix(final Component suffix) {
        components.add(suffix);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            final Component component = components.get(i);
            if (i != 0 && component instanceof Name) {
                builder.append(".");
            }
            builder.append(component);
        }
        return builder.toString();
    }

    public sealed interface Component { }

    public record Index(int index) implements Component {
        @Override
        public String toString() {
            return "[%d]".formatted(index);
        }
    }

    public record Name(String name) implements Component {
        @Override
        public String toString() {
            return name;
        }
    }
}
