package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public final class DiscordCommand {
    private static final String URL = "https://discord.com/invite/7cHfHAgGpY";

    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("discord")
            .requires(Permission.COMMAND_DISCORD::check)
            .executes(context -> {
                context.getSource().getSender().sendMessage(Message.JOIN_DISCORD.asComponent(URL)
                    .color(NamedTextColor.DARK_AQUA)
                    .hoverEvent(HoverEvent.showText(Message.CLICK_HERE.asComponent(NamedTextColor.YELLOW)))
                    .clickEvent(ClickEvent.openUrl(URL))
                );
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }
}
