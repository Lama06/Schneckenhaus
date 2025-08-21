package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class DebugCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("debug")
            .requires(Permission.COMMAND_DEBUG::check)
            .then(new BatchCreateCommand().create())
            .then(new LoadingCommand().create())
            .then(new ReloadCommand().create())
            .then(new SetModelCommand().create())
            .build();
    }
}
