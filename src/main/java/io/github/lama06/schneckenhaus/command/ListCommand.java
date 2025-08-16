package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.language.Message;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class ListCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("list")
            .requires(Permission.COMMAND_LIST::check)
            .executes(context -> {
                List<Integer> selection = SchneckenhausPlugin.INSTANCE.getCommand().getSelection(context.getSource());
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
            })
            .build();
    }
}
