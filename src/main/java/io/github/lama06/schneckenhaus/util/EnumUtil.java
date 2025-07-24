package io.github.lama06.schneckenhaus.util;

public final class EnumUtil {
    private EnumUtil() { }

    public static <T extends Enum<T>> T getNext(T value) {
        T[] values = value.getDeclaringClass().getEnumConstants();
        return values[(value.ordinal() + 1) % values.length];
    }

    public static <T extends Enum<T>> T getPrevious(T value) {
        T[] values = value.getDeclaringClass().getEnumConstants();
        if (value.ordinal() == 0) {
            return values[values.length - 1];
        }
        return values[value.ordinal() - 1];
    }

    public static String beautifyName(Enum<?> value) {
        String name = value.name();
        StringBuilder builder = new StringBuilder(name.length());
        boolean capitalize = true;
        for (int i = 0; i < name.length(); i++) {
            char letter = name.charAt(i);
            if (letter == '_') {
                builder.append(' ');
                capitalize = true;
                continue;
            }
            if (capitalize) {
                builder.append(Character.toUpperCase(letter));
                capitalize = false;
                continue;
            }
            builder.append(Character.toLowerCase(letter));
        }
        return builder.toString();
    }
}
