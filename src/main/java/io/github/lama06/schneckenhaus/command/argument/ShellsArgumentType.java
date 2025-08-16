package io.github.lama06.schneckenhaus.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.concurrent.CompletableFuture;

public final class ShellsArgumentType implements CustomArgumentType.Converted<ShellSelector, String> {
    public static final ShellsArgumentType INSTANCE = new ShellsArgumentType();

    private static final DynamicCommandExceptionType INVALID_SELECTOR =
        new DynamicCommandExceptionType(selector -> () -> Message.INVALID_SELECTOR.toString(selector));

    private ShellsArgumentType() { }

    @Override
    public ShellSelector convert(String input) throws CommandSyntaxException {
        if (input.equalsIgnoreCase("here")) {
            return ShellSelector.Here.INSTANCE;
        }
        if (input.equalsIgnoreCase("selection")) {
            return ShellSelector.Selection.INSTANCE;
        }
        try {
            return new ShellSelector.Id(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            throw INVALID_SELECTOR.create(input);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
        CommandContext<S> context,
        SuggestionsBuilder builder
    ) {
        if ("here".startsWith(builder.getRemainingLowerCase())) {
            builder.suggest("here", Message.SELECTOR_HERE_DESCRIPTION::toString);
        }
        if ("selection".startsWith(builder.getRemainingLowerCase())) {
            builder.suggest("selection", Message.SELECTOR_SELECTION_DESCRIPTION::toString);
        }
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
