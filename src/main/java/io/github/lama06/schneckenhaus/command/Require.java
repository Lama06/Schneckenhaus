package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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

    public static BlockPosition blockPosition(final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough arguments").color(ChatColor.RED).build());
            return null;
        }
        final Integer x = integer(sender, args[0]);
        if (x == null) {
            return null;
        }
        final Integer y = integer(sender, args[1]);
        if (y == null) {
            return null;
        }
        final Integer z = integer(sender, args[2]);
        if (z == null) {
            return null;
        }
        return new BlockPosition(x, y, z);
    }

    public static BlockArea blockArea(final CommandSender sender, final String[] args) {
        if (args.length < 6) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough arguments").color(ChatColor.RED).build());
            return null;
        }
        final BlockPosition first = blockPosition(sender, args);
        if (first == null) {
            return null;
        }
        final BlockPosition second = blockPosition(sender, Arrays.copyOfRange(args, 3, args.length));
        if (second == null) {
            return null;
        }
        return new BlockArea(first, second);
    }

    public static <T extends Keyed> T keyed(final Registry<T> registry, final CommandSender sender, final String arg) {
        final T keyed = registry.match(arg);
        if (keyed == null) {
            sender.spigot().sendMessage(new ComponentBuilder("Unknown key: " + arg).color(ChatColor.RED).build());
            return null;
        }
        return keyed;
    }

    public static Material material(final CommandSender sender, final String arg) {
        return keyed(Registry.MATERIAL, sender, arg);
    }

    public static Shell<?> shell(final CommandSender sender, final String arg) {
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
        final Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            sender.spigot().sendMessage(new ComponentBuilder("This snail shell doesn't exist").color(ChatColor.RED).build());
            return null;
        }
        return shell;
    }
}
