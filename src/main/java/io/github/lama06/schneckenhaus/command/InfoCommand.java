package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class InfoCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", "Provides information about the snail snell at your current location"),
                new HelpCommand.Entry("<id>", "Provides information about the snail shell at the specified id")
        );
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Shell<?> shell = Require.shell(sender, args.length == 1 ? args[0] : null);
        if (shell == null) {
            return;
        }

        TextComponent.Builder builder = Component.text()
          .append(Component.text("Snail Shell\n", NamedTextColor.YELLOW, TextDecoration.BOLD));

        for (final Entry entry : shell.getInformation()) {
            builder.append(
              Component.text(entry.key() + ": ", NamedTextColor.AQUA)
            );
            builder.append(
              Component.text(entry.value() + "\n", entry.color() != null ? entry.color() : NamedTextColor.WHITE)
            );
        }

        builder.append(
          Component.text("> Teleport <\n", NamedTextColor.LIGHT_PURPLE)
            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport")))
            .clickEvent(ClickEvent.runCommand("/sh tp " + shell.getId()))
        );

        builder.append(
          Component.text("> Give Item <", NamedTextColor.LIGHT_PURPLE)
            .hoverEvent(HoverEvent.showText(Component.text("Click to get an item connected to this snail shell")))
            .clickEvent(ClickEvent.runCommand("/sh giveItem " + shell.getId()))
        );

        sender.sendMessage(builder.build());
    }

    public record Entry(String key, String value, NamedTextColor color) {
        public Entry(String key, String value) {
            this(key, value, null);
        }
    }
}
