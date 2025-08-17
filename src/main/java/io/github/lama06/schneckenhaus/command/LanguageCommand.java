package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.language.Language;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;

public final class LanguageCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("language")
            .then(Commands.argument("language", new EnumArgumentType<>(Language.class))
                .executes(this::execute)
            )
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        Language language = context.getArgument("language", Language.class);
        plugin.getTranslator().setLanguage(language);
        context.getSource().getSender().sendMessage(Message.LANGUAGE_CHANGE_SUCCESSFUL.asComponent(NamedTextColor.GREEN, language.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
