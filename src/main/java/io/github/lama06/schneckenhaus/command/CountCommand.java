package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class CountCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("", t("cmd_count_help")));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final int count = SchneckenPlugin.INSTANCE.getWorld().getNumberOfShells();

        TextComponent.Builder builder = Component.text()
            .append(
                Component.text(t("cmd_count_result", count))
                    .clickEvent(ClickEvent.copyToClipboard(Integer.toString(count)))
                    .hoverEvent(HoverEvent.showText(Component.text(t("cmd_action_copy"))))
            );

        if (count > 0) {
            builder.appendNewline();
            builder.append(
              Component.text("> " + t("cmd_count_view_latest") + " <", NamedTextColor.LIGHT_PURPLE)
                .clickEvent(ClickEvent.runCommand("/sh info " + count))
                .hoverEvent(HoverEvent.showText(Component.text(t("cmd_action_click"))))
            );
        }

        sender.sendMessage(builder);
    }
}
