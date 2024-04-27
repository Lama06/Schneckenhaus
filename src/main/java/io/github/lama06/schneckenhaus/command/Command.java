package io.github.lama06.schneckenhaus.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Command {
    public List<HelpCommand.Entry> getHelp() {
        return List.of();
    }

    public abstract void execute(final CommandSender sender, final String[] args);

    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return List.of();
    }
}
