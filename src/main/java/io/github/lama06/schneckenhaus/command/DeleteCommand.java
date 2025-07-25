package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class DeleteCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("<id>", t("cmd_delete_help")));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final boolean confirm = args.length != 0 && args[args.length - 1].equals("confirm");
        final String shellArg = (args.length >= 1 && !args[0].equals("confirm")) ? args[0] : null;
        final Shell<?> shell = Require.shell(sender, shellArg);
        if (shell == null) {
            return;
        }
        if (!confirm) {
            Component message = Component.text()
                .append(
                    Component.text(t("cmd_delete_confirm_heading")).color(NamedTextColor.YELLOW).decorate(TextDecoration.UNDERLINED)
                )
                .appendNewline()
                .append(Component.text(t("cmd_delete_confirm_id")).color(NamedTextColor.WHITE))
                .append(Component.text(shell.getId()).color(NamedTextColor.AQUA))
                .appendNewline()
                .append(Component.text(t("cmd_delete_confirm_owner")).color(NamedTextColor.WHITE))
                .append(Component.text(shell.getCreator().getName()).color(NamedTextColor.AQUA))
                .appendNewline()
                .append(Component.text(t("cmd_delete_confirm_warning")).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(
                    Component.text("> %s <".formatted(t("cmd_delete_confirm_button")))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(HoverEvent.showText(
                            Component.text(t("cmd_delete_confirm_button_warning"))
                        ))
                        .clickEvent(ClickEvent.runCommand("/sh delete %d confirm".formatted(shell.getId())))
                )
                .build();
            sender.sendMessage(message);
            return;
        }
        shell.delete();
        sender.sendMessage(Component.text(t("cmd_delete_success"), NamedTextColor.GREEN));
    }
}
