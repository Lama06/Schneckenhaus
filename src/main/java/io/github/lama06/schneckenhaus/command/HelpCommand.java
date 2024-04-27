package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class HelpCommand extends Command {
    @Override
    public List<Entry> getHelp() {
        return List.of(new Entry("", "Shows this page"));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final ComponentBuilder builder = new ComponentBuilder();

        builder.append("-").obfuscated(true).bold(true);
        builder.append(" Schneckenhaus-Plugin ").reset().color(ChatColor.YELLOW).bold(true);
        builder.append("(Version %s) ".formatted(SchneckenPlugin.INSTANCE.getDescription().getVersion())).reset();
        final Text buildTime = new Text("Build Time: " + SchneckenPlugin.INSTANCE.getBuildProperties().time());
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, buildTime));
        builder.append("-\n").reset().obfuscated(true).bold(true);

        builder.append("Website: ").reset().color(ChatColor.AQUA).append("github.com/Lama06/Schneckenhaus\n").reset();
        builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Lama06/Schneckenhaus/"));
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Report issues, give feedback etc. here")));

        builder.append("Commands:").reset().bold(true).color(ChatColor.YELLOW);
        builder.append(" (Hover for information)").reset();

        for (final Entry entry : SchneckenPlugin.INSTANCE.getCommand().getHelp()) {
            builder.append("\n" + entry.command()).reset().color(ChatColor.AQUA);
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(entry.description())));
            builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.command()));
        }

        sender.spigot().sendMessage(builder.build());
    }

    public record Entry(String command, String description) { }
}
