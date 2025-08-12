package io.github.lama06.schneckenhaus.command.parameter;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ParameterizedCommand {
    int run(CommandContext<CommandSourceStack> context, Map<String, Object> parameters) throws CommandException;

    static CommandNode<CommandSourceStack> create(
        ArgumentBuilder<CommandSourceStack, ?> builder,
        List<CommandParameter> parameters,
        ParameterizedCommand command
    ) {
        Map<String, Object> values = new HashMap<>();

        CommandNode<CommandSourceStack> node = builder.build();
        for (CommandParameter parameter : parameters) {
            node.addChild(Commands.literal(parameter.name())
                .executes(context -> {
                    int result = command.run(context, values);
                    values.clear();
                    return result;
                })
                .then(Commands.argument(parameter.name(), parameter.type())
                    .executes(context -> {
                        int result = command.run(context, values);
                        values.clear();
                        return result;
                    })
                    .redirect(node, context -> {
                        Object value = context.getArgument(parameter.name(), Object.class);
                        if (value != null) {
                            values.put(parameter.name(), value);
                        }
                        return context.getSource();
                    })
                )
                .build()
            );
        }
        return node;
    }
}
