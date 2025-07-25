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

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class HelpCommand extends Command {
    @Override
    public List<Entry> getHelp() {
        return List.of(new Entry("", t("cmd_help_help")));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final TextComponent.Builder builder = Component.text();

        builder.append(Component.text().content("-").decorate(TextDecoration.OBFUSCATED, TextDecoration.BOLD));
        builder.append(
          Component.text()
            .content(" " + t("cmd_help_heading") + " ")
            .color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.BOLD)
            .hoverEvent(HoverEvent.showText(Component.text(t("cmd_help_heading_hover"))))
        );
        builder.append(
          Component.text()
            .content(t("cmd_help_version", SchneckenPlugin.INSTANCE.getPluginMeta().getVersion()))
            .color(NamedTextColor.YELLOW)
        );
        builder.append(Component.text("-").decorate(TextDecoration.OBFUSCATED, TextDecoration.BOLD));

        builder.appendNewline();

        builder.append(Component.text(t("cmd_help_website"), NamedTextColor.AQUA));
        builder.append(
          Component.text()
            .content("github.com/Lama06/Schneckenhaus")
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(Component.text(t("cmd_action_copy"))))
            .clickEvent(ClickEvent.openUrl("https://github.com/Lama06/Schneckenhaus"))
        );
        builder.appendNewline();
        builder.append(Component.text(t("cmd_help_contact"), NamedTextColor.AQUA));
        String mailAddress = "andreasprues36[at]gmail.com".replace("[at]", "@");
        builder.append(
          Component.text()
            .content(mailAddress)
            .decorate(TextDecoration.UNDERLINED)
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
            .clickEvent(ClickEvent.copyToClipboard(mailAddress))
        );

        builder.appendNewline();

        builder.append(Component.text(t("cmd_help_commands"), NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        builder.append(Component.text(t("cmd_help_commands_hint_hover")));

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
