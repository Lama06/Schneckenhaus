package io.github.lama06.schneckenhaus.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.*;

public class MultiplexerCommand extends Command {
    private final Map<String, Command> subCommands = new HashMap<>();
    private Command defaultSubCommand;

    public void addSubCommand(final String name, final Command subCommand, final boolean isDefault) {
        subCommands.put(name, subCommand);
        if (isDefault) {
            defaultSubCommand = subCommand;
        }
    }

    public void addSubCommand(final String name, final Command subCommand) {
        addSubCommand(name, subCommand, false);
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        final List<HelpCommand.Entry> entries = new ArrayList<>();
        for (final String name : subCommands.keySet()) {
            final Command subCommand = subCommands.get(name);
            for (final HelpCommand.Entry subCommandHelp : subCommand.getHelp()) {
                entries.add(new HelpCommand.Entry(name + " " + subCommandHelp.command(), subCommandHelp.description()));
            }
        }
        return entries;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            if (defaultSubCommand == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Not enough arguments").color(ChatColor.RED).build());
                return;
            }
            defaultSubCommand.execute(sender, args);
            return;
        }
        final Command subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            sender.spigot().sendMessage(new ComponentBuilder("Invalid sub-command: " + args[0]).color(ChatColor.RED).build());
            return;
        }
        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return subCommands.entrySet().stream().filter(entry -> !entry.getValue().isHidden()).map(Map.Entry::getKey).toList();
        }
        final Command subCommand = subCommands.get(args[0]);
        if (subCommand == null || subCommand.isHidden()) {
            return List.of();
        }
        return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
