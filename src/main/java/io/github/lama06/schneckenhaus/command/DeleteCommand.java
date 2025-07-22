package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class DeleteCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("<id>", "Deletes a snail shell"));
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
                Component.text("Confirmation to delete Snail Shell\n")
                  .color(NamedTextColor.YELLOW)
                  .decorate(TextDecoration.UNDERLINED)
              )
              .append(Component.text("Id: ").color(NamedTextColor.WHITE))
              .append(Component.text(shell.getId()).color(NamedTextColor.AQUA))
              .appendNewline()
              .append(Component.text("Owner: ").color(NamedTextColor.WHITE))
              .append(Component.text(shell.getCreator().getName()).color(NamedTextColor.AQUA))
              .appendNewline()
              .append(Component.text("This is ").color(NamedTextColor.YELLOW))
              .append(Component.text("cannot").decorate(TextDecoration.BOLD))
              .append(Component.text(" be undone!\n").color(NamedTextColor.YELLOW))
              .append(
                Component.text("> Permanently delete this snail shell <")
                  .color(NamedTextColor.RED)
                  .decorate(TextDecoration.BOLD)
                  .hoverEvent(HoverEvent.showText(
                    Component.text("This is an irreversible action!")
                  ))
                  .clickEvent(ClickEvent.runCommand("/sh delete %d confirm".formatted(shell.getId())))
              )
              .build();
            sender.sendMessage(message);
            return;
        }
        shell.delete();
        sender.sendMessage(Component.text("Deletion successful", NamedTextColor.GREEN));
    }
}
