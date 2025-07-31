package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.command.MultiplexerCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public final class DebugCommand extends MultiplexerCommand {
    public DebugCommand() {
        addSubCommand("createShells", new CreateShellsCommand());
        addSubCommand("viewData", new ViewDataCommand());
        addSubCommand("craft", new CraftCommand());
        addSubCommand("listChunkTickets", new ListChunkTicketsCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("ONLY FOR DEBUGGING!", NamedTextColor.RED));
        super.execute(sender, args);
    }
}
