package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.SchneckenWorld;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CountCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("", "Displays how many snail shells there are"));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final SchneckenWorld world = SchneckenPlugin.INSTANCE.getWorld();
        final int count = SchneckenWorld.NEXT_ID.get(world) - 1;
        final ComponentBuilder builder = new ComponentBuilder();
        builder.append("There are ");
        builder.append(Integer.toString(count)).color(ChatColor.AQUA);
        builder.event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Integer.toString(count)));
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy")));
        builder.append(" snail shells on this server.").reset();
        if (count > 0) {
            builder.append("\n> View most recently created <").color(ChatColor.LIGHT_PURPLE);
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sh info " + count));
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click here")));
        }
        sender.spigot().sendMessage(builder.build());
    }
}
