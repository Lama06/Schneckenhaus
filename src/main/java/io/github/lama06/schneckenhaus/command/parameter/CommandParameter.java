package io.github.lama06.schneckenhaus.command.parameter;

import com.mojang.brigadier.arguments.ArgumentType;

public record CommandParameter(String name, boolean optional, ArgumentType<?> type) {
    public static CommandParameter required(String name, ArgumentType<?> type) {
        return new CommandParameter(name, true, type);
    }

    public static CommandParameter optional(String name, ArgumentType<?> type) {
        return new CommandParameter(name, false, type);
    }
}
