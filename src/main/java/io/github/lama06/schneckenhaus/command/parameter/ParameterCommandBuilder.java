package io.github.lama06.schneckenhaus.command.parameter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.*;
import java.util.function.Predicate;

public final class ParameterCommandBuilder {
    private ArgumentBuilder<CommandSourceStack, ?> root;
    private final List<LiteralCommandNode<CommandSourceStack>> parameters = new ArrayList<>();
    private ParameterCommand command;
    private final Map<String, Object> argumentCache = new HashMap<>();

    ParameterCommandBuilder() { }

    public ParameterCommandBuilder root(String name) {
        root = Commands.literal(name);
        return this;
    }

    public ParameterCommandBuilder root(String argument, ArgumentType<?> type) {
        root = Commands.argument(argument, type);
        return this;
    }

    public ParameterCommandBuilder requires(Predicate<CommandSourceStack> requirement) {
        root.requires(requirement);
        return this;
    }

    public ParameterCommandBuilder parameter(LiteralCommandNode<CommandSourceStack> parameter) {
        parameters.add(parameter);
        return this;
    }

    public ParameterCommandBuilder parameter(LiteralArgumentBuilder<CommandSourceStack> builder) {
        return parameter(builder.build());
    }

    public ParameterCommandBuilder parameter(String name, ArgumentType<?> argumentType) {
        return parameter(Commands.literal(name).then(Commands.argument(name, argumentType)));
    }

    public ParameterCommandBuilder executes(ParameterCommand command) {
        this.command = command;
        return this;
    }

    public CommandNode<CommandSourceStack> build() {
        CommandNode<CommandSourceStack> root = this.root.executes(this::executeWithArguments).build();
        for (LiteralCommandNode<CommandSourceStack> parameter : parameters) {
            root.addChild(redirectToRoot(parameter, root, new HashSet<>()));
        }
        return root;
    }

    private CommandNode<CommandSourceStack> redirectToRoot(
        CommandNode<CommandSourceStack> node,
        CommandNode<CommandSourceStack> root,
        Set<String> argumentNames
    ) {
        if (node instanceof ArgumentCommandNode<?, ?> argumentNode) {
            argumentNames.add(argumentNode.getName());
        }

        Collection<CommandNode<CommandSourceStack>> children = node.getChildren();

        if (children.isEmpty()) {
            return node.createBuilder()
                .redirect(root, context -> {
                    cacheArguments(context, argumentNames);
                    return context.getSource();
                })
                .executes(context -> {
                    cacheArguments(context, argumentNames);
                    return executeWithArguments(context);
                })
                .build();
        }

        ArgumentBuilder<CommandSourceStack, ?> builder = node.createBuilder();
        builder.executes(this::executeWithArguments);
        for (CommandNode<CommandSourceStack> child : children) {
            builder.then(redirectToRoot(child, root, new HashSet<>(argumentNames)));
        }
        return builder.build();
    }

    private void cacheArguments(CommandContext<CommandSourceStack> context, Set<String> argumentNames) {
        for (String argumentName : argumentNames) {
            argumentCache.put(argumentName, context.getArgument(argumentName, Object.class));
        }
    }

    private int executeWithArguments(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            return command.run(context, argumentCache);
        } finally {
            argumentCache.clear();
        }
    }
}
