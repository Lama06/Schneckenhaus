package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenWorld;
import io.github.lama06.schneckenhaus.snell.Shell;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
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
        final Shell shell = Require.shell(sender, args.length == 1 ? args[0] : null);
        if (shell == null) {
            return;
        }

        final ComponentBuilder builder = new ComponentBuilder();
        builder.append("Snail Shell\n").color(ChatColor.YELLOW).bold(true);
        for (final Entry entry : shell.getInformation()) {
            builder.append(entry.key() + ": ").reset().color(ChatColor.AQUA);
            builder.append(entry.value() + "\n").reset();
            if (entry.color() != null) {
                builder.color(entry.color());
            }
        }

        builder.append("> Teleport <\n").reset().color(ChatColor.LIGHT_PURPLE);
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
        final Location spawnLocation = shell.getPosition().getSpawnLocation();
        final String spawnLocationText = "%d %d %d".formatted(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
        final String tpCommand = "/execute in %s run tp %s".formatted(SchneckenWorld.NAME, spawnLocationText);
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand));

        builder.append("> Give Item <").reset().color(ChatColor.LIGHT_PURPLE);
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get an item connected to this snail shell")));
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sh giveItem " + shell.getPosition().getId()));

        sender.spigot().sendMessage(builder.build());
    }

    public record Entry(String key, String value, ChatColor color) {
        public Entry(String key, String value) {
            this(key, value, null);
        }
    }
}
