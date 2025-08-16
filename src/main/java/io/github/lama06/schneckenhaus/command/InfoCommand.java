package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellInformation;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;

public final class InfoCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("info")
            .requires(Permission.COMMAND_INFO::check)
            .then(Commands.argument("shell", ShellsArgumentType.INSTANCE)
                .executes(this::execute)
            )
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Shell> shells = context.getArgument("shell", ShellSelector.class).resolve(context.getSource());
        Shell shell = shells.getFirst();

        TextComponent.Builder builder = Component.text();

        builder.append(Message.SNAIL_SHELL.asComponent(NamedTextColor.YELLOW));

        for (ShellInformation information : shell.getInformation()) {
            builder.appendNewline();
            builder.append(information.name().colorIfAbsent(NamedTextColor.AQUA));
            builder.append(Component.text(": "));
            builder.append(information.value()
                .hoverEvent(HoverEvent.showText(Message.CLICK_TO_COPY.asComponent(NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.copyToClipboard(PlainTextComponentSerializer.plainText().serialize(information.value())))
            );
        }

        builder.appendNewline();
        builder.append(MiniMessage.miniMessage().deserialize("> <1> <", Placeholder.unparsed("1", Message.TELEPORT.toString()))
            .color(NamedTextColor.YELLOW)
            .hoverEvent(HoverEvent.showText(Message.CLICK_HERE.asComponent(NamedTextColor.YELLOW)))
            .clickEvent(ClickEvent.runCommand("/sh tp " + shell.getId()))
        );

        builder.appendNewline();
        builder.append(MiniMessage.miniMessage().deserialize("> <1> <", Placeholder.unparsed("1", Message.OPEN_MENU.toString()))
            .color(NamedTextColor.YELLOW)
            .hoverEvent(HoverEvent.showText(Message.CLICK_HERE.asComponent(NamedTextColor.YELLOW)))
            .clickEvent(ClickEvent.runCommand("/sh menu " + shell.getId()))
        );

        builder.appendNewline();
        builder.append(MiniMessage.miniMessage().deserialize("> <1> <", Placeholder.unparsed("1", Message.GET_ITEM.toString()))
            .color(NamedTextColor.YELLOW)
            .hoverEvent(HoverEvent.showText(Message.CLICK_HERE.asComponent(NamedTextColor.YELLOW)))
            .clickEvent(ClickEvent.runCommand("/sh item " + shell.getId()))
        );

        context.getSource().getSender().sendMessage(builder);
        return shell.getTotalAccessCount() % 15;
    }
}
