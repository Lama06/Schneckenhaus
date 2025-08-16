package io.github.lama06.schneckenhaus.command.select;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class SelectionCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("selection")
            .requires(Permission.COMMAND_SELECTION::check)
            .then(Commands.literal("list")
                .executes(this::list)
            )
            .then(Commands.literal("clear")
                .executes(this::clear)
            )
            .build();
    }

    private int list(CommandContext<CommandSourceStack> context) {
        List<Integer> selection = plugin.getCommand().getSelection(context.getSource());
        TextComponent.Builder builder = Component.text();
        builder.append(Message.SELECTION.asComponent(NamedTextColor.AQUA)).append(Component.text(":"));
        if (selection.isEmpty()) {
            builder.append(Component.space()).append(Message.EMPTY.asComponent(NamedTextColor.RED));
        }
        for (int id : selection) {
            builder.appendSpace();
            builder.append(Component.text(id)
                .hoverEvent(HoverEvent.showText(Message.CLICK_FOR_DETAILS.asComponent(NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.runCommand("/sh info " + id))
            );
        }
        context.getSource().getSender().sendMessage(builder);
        return selection.size();
    }

    private int clear(CommandContext<CommandSourceStack> context) {
        plugin.getCommand().setSelection(context.getSource(), List.of());
        context.getSource().getSender().sendMessage(Message.CLEAR_SELECTION_SUCCESS.asComponent(NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
