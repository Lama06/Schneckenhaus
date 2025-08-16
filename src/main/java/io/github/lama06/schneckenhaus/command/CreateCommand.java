package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommand;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.player.ShellTeleportOptions;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellCreationType;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.util.ConcurrencyUtils;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class CreateCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        LiteralArgumentBuilder<CommandSourceStack> createCommandBuilder = Commands.literal("create")
            .requires(Permission.COMMAND_CREATE::check);
        for (ShellFactory factory : ShellFactories.getFactories()) {
            ParameterCommandBuilder typeCommandBuilder = ParameterCommand.builder(factory.getId());
            factory.addCommandParameters(typeCommandBuilder);
            typeCommandBuilder.executes((context, parameters) -> execute(factory, context, parameters));
            createCommandBuilder.then(typeCommandBuilder.build());
        }
        return createCommandBuilder.build();
    }

    private int execute(
        ShellFactory factory,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        ShellBuilder shellBuilder = factory.newBuilder();
        shellBuilder.setCreationType(ShellCreationType.COMMAND);
        if (context.getSource().getExecutor() instanceof Player player) {
            shellBuilder.setCreator(player.getUniqueId());
            shellBuilder.setOwner(player.getUniqueId());
        }
        factory.parseCommandParameters(shellBuilder, context, parameters);
        shellBuilder.build().thenAcceptAsync(
            shell -> {
                CommandSender sender = context.getSource().getSender();
                sender.sendMessage(Message.CREATE_SHELL_SUCCESS.asComponent(NamedTextColor.GREEN, shell.getId()));
                if (sender instanceof Player player) {
                    player.give(shell.createItem());
                    ShellTeleportOptions options = new ShellTeleportOptions();
                    options.setStorePreviousPositionWhenNesting(false);
                    new SchneckenhausPlayer(player).enter(shell, options);
                }
            },
            ConcurrencyUtils::runOnMainThread
        );
        return Command.SINGLE_SUCCESS;
    }
}
