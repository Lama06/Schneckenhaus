package io.github.lama06.schneckenhaus.util;

public record Range(Integer min, Integer max) {
    public static final Range POSITIVE = new Range(1, null);
    public static final Range ALL = new Range(null, null);

    public Range {
        if (min != null && max != null && max < min) {
            throw new IllegalArgumentException();
        }
    }

    public boolean contains(final int integer) {
        return (min == null || integer >= min) && (max == null || integer <= max);
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
