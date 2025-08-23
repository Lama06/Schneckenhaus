package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.ui.ShellScreen;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class MenuCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("menu")
            .requires(Permission.COMMAND_MENU::check)
            .executes(this::execute)
            .then(Commands.argument("shell", ShellsArgumentType.INSTANCE)
                .executes(this::execute)
            )
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Shell shell = ShellSelector.getSelectorOrHere(context, "shell").resolve(context.getSource()).getFirst();
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            context.getSource().getSender().sendMessage(Message.COMMAND_ERROR_NOT_PLAYER.asComponent(NamedTextColor.RED));
            return 0;
        }
        new ShellScreen(shell, player).open();
        return Command.SINGLE_SUCCESS;
    }
}
