package io.github.lama06.schneckenhaus.util;

import java.util.Map;

public record IntegerRange(Integer min, Integer max) {
    public static final IntegerRange POSITIVE = new IntegerRange(1, null);
    public static final IntegerRange ALL = new IntegerRange(null, null);
    public static final IntegerRange NONE = new IntegerRange(1, 0);

    public static IntegerRange deserialize(Object object) {
        return switch (object) {
            case String string -> {
                string = string.strip();
                if (string.equals("all")) {
                    yield ALL;
                }
                if (string.equals("none")) {
                    yield NONE;
                }
                try {
                    int integer = Integer.parseInt(string);
                    yield new IntegerRange(integer, integer);
                } catch (NumberFormatException ignored) { }
                if (string.startsWith("<=")) {
                    try {
                        int max = Integer.parseInt(string.substring(2).strip());
                        yield new IntegerRange(null, max);
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
                if (string.startsWith(">=")) {
                    try {
                        int min = Integer.parseInt(string.substring(2).strip());
                        yield new IntegerRange(min, null);
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
                yield null;
            }
            case Map<?, ?> map -> {
                if (!(map.get("min") instanceof Integer min) || !(map.get("max") instanceof Integer max)) {
                    yield null;
                }
                yield new IntegerRange(min, max);
            }
            case null, default -> null;
        };
    }

    public boolean contains(final int integer) {
        return (min == null || integer >= min) && (max == null || integer <= max);
    }

    public Object serialize() {
        if (min == null && max == null) {
            return "all";
        }
        if (min == null) {
            return "<= " + max;
        }
        if (max == null) {
            return ">= " + min;
        }
        if (min.equals(max)) {
            return min;
        }
        if (max < min) {
            return "none";
        }
        return Map.of(
            "min", min,
            "max", max
        );
    }

    @Override
    public String toString() {
        if (min != null && max != null) {
            return "in [%d, %d]".formatted(min, max);
        }
        if (min != null) {
            return ">= " + min;
        }
        if (max != null) {
            return "<= " + max;
        }
        return "";
    }
}
