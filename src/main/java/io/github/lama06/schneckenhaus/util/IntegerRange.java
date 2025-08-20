package io.github.lama06.schneckenhaus.util;

import java.util.Map;

public record IntegerRange(Integer min, Integer max) {
    public static final IntegerRange POSITIVE = new IntegerRange(1, null);
    public static final IntegerRange ALL = new IntegerRange(null, null);
    public static final IntegerRange NONE = new IntegerRange(1, 0);

    public static IntegerRange deserialize(Object config) {
        if (!(config instanceof Map<?,?> map)) {
            return null;
        }
        Integer min = null, max = null;
        if (map.get("min") instanceof Integer integer) {
            min = integer;
        }
        if (map.get("max") instanceof Integer integer) {
            max = integer;
        }
        return new IntegerRange(min, max);
    }

    public boolean contains(int integer) {
        return (min == null || integer >= min) && (max == null || integer <= max);
    }

    public Object serialize() {
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
