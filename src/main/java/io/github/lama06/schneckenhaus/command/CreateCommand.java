package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellConfig;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
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
            sender.sendMessage(Component.text("Specify a snell type: " + shellTypes, NamedTextColor.RED));
            return;
        }
        final ShellFactory<?> factory = ShellFactories.getByName(args[0]);
        if (factory == null) {
            sender.sendMessage(Component.text("Invalid shell type: " + args[0], NamedTextColor.RED));
            return;
        }
        execute(player, args, factory);
    }

    private <C extends ShellConfig> void execute(final Player player, final String[] args, final ShellFactory<C> factory) {
        final C config = factory.parseConfig(player, Arrays.copyOfRange(args, 1, args.length));
        if (config == null) {
            return;
        }
        final Shell<C> shell = SchneckenPlugin.INSTANCE.getWorld().createShell(factory, player, config);
        final Location previousLocation = player.getLocation();
        player.teleport(shell.getSpawnLocation());
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        schneckenPlayer.pushPreviousLocation(previousLocation, false);
        if (!player.getInventory().addItem(shell.createItem()).isEmpty()) {
            player.sendMessage(Component.text("Your inventory is full", NamedTextColor.RED));
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
