package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.shell.Shell;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
            final ComponentBuilder builder = new ComponentBuilder();
            builder.append("Confirmation to delete Snail Shell\n").underlined(true).color(ChatColor.YELLOW);
            builder.append("Id: ").reset().append(Integer.toString(shell.getId())).color(ChatColor.AQUA).append("\n");
            builder.append("Owner: ").reset().append(shell.getCreator().getName()).color(ChatColor.AQUA).append("\n");
            builder.append("This is ").reset().color(ChatColor.YELLOW).append("cannot").bold(true).append(" be undone!\n").bold(false);
            builder.append("> Permanently delete this snail shell <").color(ChatColor.RED).bold(true);
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("This is an irreversible action!")));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sh delete %d confirm".formatted(shell.getId())));
            sender.spigot().sendMessage(builder.build());
            return;
        }
        shell.delete();
        sender.spigot().sendMessage(new ComponentBuilder("Deletion successful").color(ChatColor.GREEN).build());
    }
}
