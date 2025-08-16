package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TagCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("tag")
            .requires(Permission.COMMAND_TAG::check)
            .then(Commands.argument("shells", ShellsArgumentType.INSTANCE)
                .then(Commands.literal("list")
                    .executes(this::list)
                )
                .then(Commands.literal("add")
                    .then(Commands.argument("tag", StringArgumentType.string())
                        .executes(this::add)
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("tag", StringArgumentType.string())
                        .executes(this::remove)
                        .suggests(this::removeSuggestions)
                    )
                )
            )
            .build();
    }

    private int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Shell> shells = context.getArgument("shells", ShellSelector.class).resolve(context.getSource());
        TextComponent.Builder builder = Component.text();
        builder.append(Message.TAGS.asComponent(NamedTextColor.YELLOW));
        for (Shell shell : shells) {
            builder.appendNewline();
            builder.append(Component.text(Message.SNAIL_SHELL + " " + shell.getId(), NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Message.CLICK_TO_EDIT.asComponent(NamedTextColor.YELLOW)))
                .clickEvent(ClickEvent.suggestCommand("/sh tag %s ".formatted(shell.getId())))
            );
            builder.append(Component.text(":"));
            for (String tag : shell.getTags()) {
                builder.appendSpace();
                builder.append(Component.text(tag)
                    .hoverEvent(HoverEvent.showText(Message.CLICK_TO_REMOVE.asComponent(NamedTextColor.YELLOW)))
                    .clickEvent(ClickEvent.suggestCommand("/sh tag %s remove %s".formatted(shell.getId(), tag)))
                );
            }
            if (shell.getTags().isEmpty()) {
                builder.appendSpace().append(Message.EMPTY.asComponent(NamedTextColor.RED)
                    .hoverEvent(HoverEvent.showText(Message.CLICK_TO_ADD.asComponent(NamedTextColor.YELLOW)))
                    .clickEvent(ClickEvent.suggestCommand("/sh tag %s add ".formatted(shell.getId())))
                );
            }
        }
        context.getSource().getSender().sendMessage(builder);
        return shells.size();
    }

    private int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Shell> shells = context.getArgument("shells", ShellSelector.class).resolve(context.getSource());
        String tag = StringArgumentType.getString(context, "tag");
        for (Shell shell : shells) {
            shell.addTag(tag);
        }
        context.getSource().getSender().sendMessage(Message.TAG_ADD_SUCCESS.asComponent(NamedTextColor.GREEN, tag, shells.size()));
        return shells.size();
    }

    private int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Shell> shells = context.getArgument("shells", ShellSelector.class).resolve(context.getSource());
        String tag = StringArgumentType.getString(context, "tag");
        for (Shell shell : shells) {
            shell.removeTag(tag);
        }
        context.getSource().getSender().sendMessage(Message.TAG_REMOVE_SUCCESS.asComponent(NamedTextColor.GREEN, tag, shells.size()));
        return shells.size();
    }

    private CompletableFuture<Suggestions> removeSuggestions(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        Shell shell = context.getArgument("shells", ShellSelector.class).resolve(context.getSource()).getFirst();
        shell.getTags().forEach(builder::suggest);
        return builder.buildFuture();
    }
}
