package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class InfoCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", t("cmd_info_help")),
                new HelpCommand.Entry("<id>", t("cmd_info_help_id"))
        );
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Shell<?> shell = Require.shell(sender, args.length == 1 ? args[0] : null);
        if (shell == null) {
            return;
        }

        TextComponent.Builder builder = Component.text()
            .append(Component.text(t("cmd_info_heading"), NamedTextColor.YELLOW, TextDecoration.BOLD))
            .appendNewline();

        for (final Entry entry : shell.getInformation()) {
            builder.append(
              Component.text(entry.key() + ": ", NamedTextColor.AQUA)
            );
            builder.append(
              Component.text(entry.value() + "\n", entry.color() != null ? entry.color() : NamedTextColor.WHITE)
                  .hoverEvent(HoverEvent.showText(Component.text(t("cmd_action_copy"))))
                  .clickEvent(ClickEvent.copyToClipboard(entry.value()))
            );
        }

        builder.append(
          Component.text("> %s <\n".formatted(t("cmd_info_teleport")), NamedTextColor.LIGHT_PURPLE)
            .hoverEvent(HoverEvent.showText(Component.text(t("cmd_info_teleport_hover"))))
            .clickEvent(ClickEvent.runCommand("/sh tp " + shell.getId()))
        );

        builder.append(
          Component.text("> %s <".formatted(t("cmd_info_give_item")), NamedTextColor.LIGHT_PURPLE)
            .hoverEvent(HoverEvent.showText(Component.text(t("cmd_info_give_item_hover"))))
            .clickEvent(ClickEvent.runCommand("/sh giveItem " + shell.getId()))
        );

        sender.sendMessage(builder.build());
    }

    public record Entry(String key, String value, TextColor color) {
        public Entry(String key, String value) {
            this(key, value, null);
        }
    }
}
