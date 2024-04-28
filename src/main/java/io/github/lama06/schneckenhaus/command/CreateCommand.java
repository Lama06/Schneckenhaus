package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CreateCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        final String description = "Creates a new snail shell";
        final List<HelpCommand.Entry> entries = new ArrayList<>();
        for (final ShellFactory<?> factory : ShellFactories.getFactories()) {
            for (final String configTemplate : factory.getConfigCommandTemplates()) {
                final String command = "%s %s".formatted(factory.getName(), configTemplate);
                entries.add(new HelpCommand.Entry(command, description));
            }
        }
        return entries;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (args.length == 0) {
            final String shellTypes = ShellFactories.getFactories().stream().map(ShellFactory::getName).collect(Collectors.joining(", "));
            sender.spigot().sendMessage(new ComponentBuilder("Specify a snell type: " + shellTypes).color(ChatColor.RED).build());
            return;
        }
        final ShellFactory<?> factory = ShellFactories.getByName(args[0]);
        if (factory == null) {
            sender.spigot().sendMessage(new ComponentBuilder("Invalid shell type: " + args[0]).color(ChatColor.RED).build());
            return;
        }
        execute(player, args, factory);
    }

    private <C extends ShellConfig> void execute(final Player player, final String[] args, final ShellFactory<C> factory) {
        final C config = factory.parseConfig(player, Arrays.copyOfRange(args, 1, args.length));
        if (config == null) {
            return;
        }
        final Shell shell = SchneckenPlugin.INSTANCE.getWorld().createShell(factory, player, config);
        player.teleport(shell.getPosition().getSpawnLocation());
        if (!player.getInventory().addItem(shell.createItem()).isEmpty()) {
            player.spigot().sendMessage(new ComponentBuilder("Your inventory is full").color(ChatColor.RED).build());
        }
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return ShellFactories.getFactories().stream().map(ShellFactory::getName).toList();
        }
        final ShellFactory<?> factory = ShellFactories.getByName(args[0]);
        if (factory == null) {
            return List.of();
        }
        return factory.tabCompleteConfig(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
