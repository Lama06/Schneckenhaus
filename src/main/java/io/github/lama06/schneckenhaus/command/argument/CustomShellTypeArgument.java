package io.github.lama06.schneckenhaus.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.concurrent.CompletableFuture;

public final class CustomShellTypeArgument extends ConstantsHolder implements CustomArgumentType.Converted<String, String> {
    public static final CustomShellTypeArgument INSTANCE = new CustomShellTypeArgument();

    private static final DynamicCommandExceptionType INVALID_TYPE =
        new DynamicCommandExceptionType(name -> () -> Message.INVALID_CUSTOM_SHELL_TYPE.toString(name));

    private CustomShellTypeArgument() { }

    @Override
    public String convert(String input) throws CommandSyntaxException {
        if (!config.getCustom().containsKey(input)) {
            throw INVALID_TYPE.create(input);
        }
        return input;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        config.getCustom().keySet().forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
