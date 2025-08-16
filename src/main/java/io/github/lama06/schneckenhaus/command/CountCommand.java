package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;

public final class CountCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("count")
            .requires(Permission.COMMAND_COUNT::check)
            .then(Commands.literal("total")
                .executes(this::total)
            )
            .then(Commands.literal("world")
                .then(Commands.argument("world", ArgumentTypes.world())
                    .executes(this::world)
                )
            )
            .build();
    }

    private int total(CommandContext<CommandSourceStack> context) {
        int allTime = plugin.getShellManager().getAllTimeShellCount();
        int current = plugin.getShellManager().getCurrentShellCount();
        TextComponent.Builder builder = Component.text();
        builder.append(Message.ALL_TIME_COUNT.asComponent(NamedTextColor.YELLOW))
            .append(Component.text(": " + allTime)).appendNewline();
        builder.append(Message.CURRENT_COUNT.asComponent(NamedTextColor.YELLOW))
            .append(Component.text(": " + current));
        context.getSource().getSender().sendMessage(builder);
        return current;
    }

    private int world(CommandContext<CommandSourceStack> context) {
        World world = context.getArgument("world", World.class);
        int count = plugin.getShellManager().getWorldShellCount(world.getName());
        context.getSource().getSender().sendMessage(Message.WORLD_COUNT.asComponent(NamedTextColor.GREEN, count, world.getName()));
        return count;
    }
}
