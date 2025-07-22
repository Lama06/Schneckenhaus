package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CountCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("", "Displays how many snail shells there are"));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final int count = SchneckenPlugin.INSTANCE.getWorld().getNumberOfShells();

        TextComponent.Builder builder = Component.text()
          .append(Component.text("There are "))
          .append(
            Component.text(count, NamedTextColor.AQUA)
              .clickEvent(ClickEvent.copyToClipboard(Integer.toString(count)))
              .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
          )
          .append(Component.text(" snail shells on this server."));

        if (count > 0) {
            builder.appendNewline();
            builder.append(
              Component.text("> View most recently created <", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.runCommand("/sh info " + count))
                .hoverEvent(HoverEvent.showText(Component.text("Click here")))
            );
        }

        sender.sendMessage(builder);
    }
}
