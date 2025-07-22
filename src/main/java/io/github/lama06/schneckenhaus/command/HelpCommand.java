package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class HelpCommand extends Command {
    @Override
    public List<Entry> getHelp() {
        return List.of(new Entry("", "Shows this page"));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final TextComponent.Builder builder = Component.text();

        builder.append(Component.text().content("-").decorate(TextDecoration.OBFUSCATED, TextDecoration.BOLD));
        builder.append(
          Component.text()
            .content(" Schneckenhaus-Plugin ")
            .color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.BOLD)
            .hoverEvent(HoverEvent.showText(Component.text("Made with <3 by Lama06!")))
        );
        builder.append(
          Component.text()
            .content("(Version %s) ".formatted(SchneckenPlugin.INSTANCE.getPluginMeta().getVersion()))
            .color(NamedTextColor.YELLOW)
        );
        builder.append(Component.text("-").decorate(TextDecoration.OBFUSCATED, TextDecoration.BOLD));

        builder.appendNewline();

        builder.append(Component.text("Website: ", NamedTextColor.AQUA));
        builder.append(
          Component.text()
            .content("github.com/Lama06/Schneckenhaus")
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(Component.text("Click to open")))
            .clickEvent(ClickEvent.openUrl("https://github.com/Lama06/Schneckenhaus"))
        );
        builder.appendNewline();
        builder.append(Component.text("Contact: ", NamedTextColor.AQUA));
        String mailAddress = "andreasprues36[at]gmail.com".replace("[at]", "@");
        builder.append(
          Component.text()
            .content(mailAddress)
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
            .clickEvent(ClickEvent.copyToClipboard(mailAddress))
        );

        builder.appendNewline();

        builder.append(Component.text("Commands: ", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        builder.append(Component.text(" (Hover for information)"));

        for (final Entry entry : SchneckenPlugin.INSTANCE.getCommand().getHelp()) {
            builder.appendNewline();
            builder.append(
              Component.text(entry.command(), NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(Component.text(entry.description())))
                .clickEvent(ClickEvent.suggestCommand(entry.command()))
            );
        }

        sender.sendMessage(builder);
    }

    public record Entry(String command, String description) { }
}
