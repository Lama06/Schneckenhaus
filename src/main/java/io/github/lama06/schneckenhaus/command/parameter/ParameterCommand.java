package io.github.lama06.schneckenhaus.command.parameter;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Map;

public interface ParameterCommand {
    static ParameterCommandBuilder builder() {
        return new ParameterCommandBuilder();
    }

    static ParameterCommandBuilder builder(String name) {
        return builder().root(name);
    }

    int run(CommandContext<CommandSourceStack> context, Map<String, Object> parameters) throws CommandSyntaxException;
}
