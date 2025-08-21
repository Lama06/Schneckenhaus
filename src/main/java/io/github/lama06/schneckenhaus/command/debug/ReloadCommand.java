package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class ReloadCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("reload")
            .executes(context -> {
                plugin.getConfigManager().load();
                plugin.getTranslator().loadConfig();
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }
}
