package io.github.lama06.schneckenhaus.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.concurrent.CompletableFuture;

public final class ShellTypeArgumentType implements CustomArgumentType.Converted<ShellFactory, String> {
    public static final ShellTypeArgumentType INSTANCE = new ShellTypeArgumentType();

    private static final DynamicCommandExceptionType UNKNOWN_SHELL_TYPE =
        new DynamicCommandExceptionType(name -> () -> Message.INVALID_SHELL_TYPE.toString(name));

    private ShellTypeArgumentType() { }

    @Override
    public ShellFactory convert(String name) throws CommandSyntaxException {
        ShellFactory factory = ShellFactories.getByName(name);
        if (factory == null) {
            throw UNKNOWN_SHELL_TYPE.create(name);
        }
        return factory;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ShellFactories.getFactories().stream()
            .map(ShellFactory::getId)
            .filter(name -> name.startsWith(builder.getRemainingLowerCase()))
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
