package io.github.lama06.schneckenhaus.command;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class TabComplete {
    private TabComplete() { }

    public static List<String> keyed(final Registry<?> registry, final String arg) {
        return registry.stream().map(Keyed::getKey).map(NamespacedKey::toString).filter(arg::startsWith).toList();
    }

    public static List<String> material(final String arg) {
        return keyed(Registry.MATERIAL, arg);
    }

    public static List<String> blockPosition(final CommandSender sender, final String[] args) {
        if (!(sender instanceof final Player player)) {
            return List.of();
        }
        final Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock == null) {
            return List.of();
        }
        final String completion = switch (args.length) {
            case 0, 1 -> Integer.toString(targetBlock.getX());
            case 2 -> Integer.toString(targetBlock.getY());
            case 3 -> Integer.toString(targetBlock.getZ());
            default -> null;
        };
        return Optional.ofNullable(completion).stream().toList();
    }

    public static List<String> blockArea(final CommandSender sender, final String[] args) {
        if (args.length <= 3) {
            return blockPosition(sender, args);
        }
        return blockPosition(sender, Arrays.copyOfRange(args, 3, args.length));
    }
}
