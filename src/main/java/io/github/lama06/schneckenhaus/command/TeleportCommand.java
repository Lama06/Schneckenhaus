package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.player.ShellTeleportOptions;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class TeleportCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("tp")
            .requires(Permission.COMMAND_TP::check)
            .then(Commands.literal("shell")
                .then(Commands.argument("shell", ShellsArgumentType.INSTANCE)
                    .executes(this::teleportShell)
                )
            )
            .then(Commands.literal("world")
                .then(Commands.argument("world", ArgumentTypes.world())
                    .suggests((context, builder) -> {
                        config.getWorlds().keySet().forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(this::teleportWorld)
                )
            )
            .build();
    }

    private int teleportShell(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = CommandUtils.requirePlayer(context.getSource());

        Shell shell = context.getArgument("shell", ShellSelector.class).resolve(context.getSource()).getFirst();
        ShellTeleportOptions teleportOptions = new ShellTeleportOptions();
        teleportOptions.setStorePreviousPositionWhenNesting(false);
        teleportOptions.setCheckGeneralEnterPermission(false);
        new SchneckenhausPlayer(player).enter(shell, teleportOptions);

        return Command.SINGLE_SUCCESS;
    }

    private int teleportWorld(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = CommandUtils.requirePlayer(context.getSource());

        World world = context.getArgument("world", World.class);
        Block block = world.getBlockAt(-10, 0, -10);
        if (block.isEmpty()) {
            block.setType(Material.STONE);
        }
        player.teleport(block.getLocation().toCenterLocation().add(0, 1, 0));

        return Command.SINGLE_SUCCESS;
    }
}
