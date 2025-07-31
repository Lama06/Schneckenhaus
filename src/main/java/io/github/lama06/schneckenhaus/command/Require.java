package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.Range;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class Require {
    private Require() { }

    public static Player player(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component.text(t("cmd_error_not_player"), NamedTextColor.RED));
            return null;
        }
        return player;
    }

    public static Player player(CommandSender sender, String arg) {
        Player player = Bukkit.selectEntities(sender, arg).stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> (Player) entity)
            .findAny().orElse(null);
        if (player == null) {
            sender.sendMessage(Component.text(t("cmd_error_invalid_player") + arg, NamedTextColor.RED));
            return null;
        }
        return player;
    }

    public static Integer integer(final CommandSender sender, final String arg, final Range range) {
        final int integer;
        try {
            integer = Integer.parseInt(arg);
        } catch (final NumberFormatException e) {
            sender.sendMessage(Component.text(t("cmd_error_integer_invalid") + arg, NamedTextColor.RED));
            return null;
        }
        if (!range.contains(integer)) {
            sender.sendMessage(Component.text(t("cmd_error_integer_range", integer, range), NamedTextColor.RED));
            return null;
        }
        return integer;
    }

    public static Integer integer(final CommandSender sender, final String arg) {
        return integer(sender, arg, Range.ALL);
    }

    public static BlockPosition blockPosition(final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text(t("cmd_error_missing_arguments"), NamedTextColor.RED));
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
            sender.sendMessage(Component.text(t("cmd_error_missing_arguments"), NamedTextColor.RED));
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
        final T keyed = registry.get(NamespacedKey.fromString(arg));
        if (keyed == null) {
            sender.sendMessage(Component.text(t("cmd_error_unknown_key") + arg, NamedTextColor.RED));
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
                sender.sendMessage(Component.text(t("cmd_error_missing_shell"), NamedTextColor.RED));
                return null;
            }
            id = position.getId();
        } else {
            final Integer parsedId = integer(sender, arg, new Range(1, null));
            if (parsedId == null) {
                return null;
            }
            id = parsedId;
        }
        final Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            sender.sendMessage(Component.text(t("cmd_error_shell_not_found"), NamedTextColor.RED));
            return null;
        }
        return shell;
    }
}
