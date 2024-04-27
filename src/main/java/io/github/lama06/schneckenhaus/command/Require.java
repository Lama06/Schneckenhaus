package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.snell.Shell;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Require {
    public static Player player(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.spigot().sendMessage(new ComponentBuilder().append("You are not a player").color(ChatColor.RED).build());
            return null;
        }
        return player;
    }

    public static Integer integer(final CommandSender sender, final String arg, final Integer minInclusive, final Integer maxInclusive) {
        final int integer;
        try {
            integer = Integer.parseInt(arg);
        } catch (final NumberFormatException e) {
            sender.spigot().sendMessage(new ComponentBuilder("Invalid integer: " + arg).color(ChatColor.RED).build());
            return null;
        }
        if (minInclusive != null && integer < minInclusive) {
            sender.spigot().sendMessage(new ComponentBuilder("Integer must be >= " + minInclusive).color(ChatColor.RED).build());
            return null;
        }
        if (maxInclusive != null && integer > maxInclusive) {
            sender.spigot().sendMessage(new ComponentBuilder("Integer must be <= " + maxInclusive).color(ChatColor.RED).build());
            return null;
        }
        return integer;
    }

    public static Integer integer(final CommandSender sender, final String arg) {
        return integer(sender, arg, null, null);
    }

    public static Shell shell(final CommandSender sender, final String arg) {
        final int id;
        if (arg == null) {
            final Player player = player(sender);
            if (player == null) {
                return null;
            }
            final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
            if (position == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Move to a snail shell or enter an id").color(ChatColor.RED).build());
                return null;
            }
            id = position.getId();
        } else {
            final Integer parsedId = integer(sender, arg, 1, null);
            if (parsedId == null) {
                return null;
            }
            id = parsedId;
        }
        final Shell shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            sender.spigot().sendMessage(new ComponentBuilder("This snail shell doesn't exist").color(ChatColor.RED).build());
            return null;
        }
        return shell;
    }
}
