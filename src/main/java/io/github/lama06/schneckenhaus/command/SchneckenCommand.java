package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.debug.DebugCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public final class SchneckenCommand extends MultiplexerCommand {
    private static final String NAME = "schneckenhaus";

    public SchneckenCommand() {
        addSubCommand("help", new HelpCommand(), true);
        addSubCommand("create", new CreateCommand());
        addSubCommand("info", new InfoCommand());
        addSubCommand("giveItem", new GiveItemCommand());
        addSubCommand("count", new CountCommand());
        addSubCommand("newShellType", new NewShellTypeCommand());
        addSubCommand("tp", new TeleportCommand());
        if (SchneckenPlugin.INSTANCE.getBuildProperties().debug()) {
            addSubCommand("debug", new DebugCommand());
        }

        final Executor executor = new Executor();
        final PluginCommand command = Bukkit.getPluginCommand(NAME);
        command.setTabCompleter(executor);
        command.setExecutor(executor);
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        final List<HelpCommand.Entry> entries = new ArrayList<>();
        for (final HelpCommand.Entry entry : super.getHelp()) {
            entries.add(new HelpCommand.Entry("/" + NAME + " " + entry.command(), entry.description()));
        }
        return entries;
    }

    private final class Executor implements TabExecutor {
        @Override
        public boolean onCommand(
                final CommandSender sender,
                final Command command,
                final String label,
                final String[] args
        ) {
            execute(sender, args);
            return true;
        }

        @Override
        public List<String> onTabComplete(
                final CommandSender sender,
                final Command command,
                final String label,
                final String[] args
        ) {
            return tabComplete(sender, args);
        }
    }
}
