package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class DeleteCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("delete")
            .requires(Commands.restricted(Permission.COMMAND_DELETE::check))
            .executes(this::execute)
            .then(Commands.argument("shells", ShellsArgumentType.INSTANCE)
                .requires(Commands.restricted(Permission.COMMAND_DELETE::check))
                .executes(this::execute)
            )
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Shell> shells = ShellSelector.getSelectorOrHere(context, "shells").resolve(context.getSource());
        for (Shell shell : shells) {
            shell.delete();
        }
        context.getSource().getSender().sendMessage(Message.DELETE_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN, shells.size()));
        return shells.size();
    }
}
