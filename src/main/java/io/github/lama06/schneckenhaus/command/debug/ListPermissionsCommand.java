package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class ListPermissionsCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("list-permissions")
            .executes(context -> {
                Permission.generateDocs();
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }
}
