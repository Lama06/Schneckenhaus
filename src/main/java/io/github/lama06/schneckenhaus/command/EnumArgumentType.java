package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class EnumArgumentType<T extends Enum<T>> implements CustomArgumentType.Converted<T, String> {
    private final Class<T> type;

    public EnumArgumentType(Class<T> type) {
        this.type = type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Arrays.stream(type.getEnumConstants()).map(T::name).map(name -> name.toLowerCase(Locale.ROOT)).forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public T convert(String string) throws CommandSyntaxException {
        return Arrays.stream(type.getEnumConstants())
            .filter(constant -> constant.name().equalsIgnoreCase(string))
            .findAny()
            .orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create());
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
