package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.List;

public final class ItemCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("item")
            .requires(Permission.COMMAND_ITEM::check)
            .then(Commands.argument("shells", ShellsArgumentType.INSTANCE)
                .executes(this::execute)
            )
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = CommandUtils.requirePlayer(context.getSource());
        List<Shell> shells = context.getArgument("shells", ShellSelector.class).resolve(context.getSource());
        for (Shell shell : shells) {
            player.give(shell.createItem());
        }
        return shells.size();
    }
}
