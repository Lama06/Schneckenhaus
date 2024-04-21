package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public final class SchneckenCommand implements TabExecutor {
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            root(sender);
            return true;
        }
        final String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "help" -> root(sender);
            case "create" -> create(sender, remainingArgs);
            case "info" -> info(sender, remainingArgs);
            case "giveShulker" -> giveShulker(sender, remainingArgs);
            case "debugCreateShells" -> debugCreateShells(sender, remainingArgs);
            default -> sender.spigot().sendMessage(new ComponentBuilder("Invalid command").color(ChatColor.RED).build());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("help", "create", "info", "giveShulker");
        }
        final String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        return switch (args[0]) {
            case "create" -> createTabComplete(sender, remainingArgs);
            default -> List.of();
        };
    }

    private void root(final CommandSender sender) {
        final ComponentBuilder builder = new ComponentBuilder();

        builder.append("-").obfuscated(true).bold(true);
        builder.append(" Schneckenhaus-Plugin ").reset().color(ChatColor.YELLOW).bold(true);
        builder.append("(Version %s) ".formatted(SchneckenPlugin.INSTANCE.getDescription().getVersion())).reset();
        builder.append("-\n").reset().obfuscated(true).bold(true);

        builder.append("Website: ").reset().color(ChatColor.AQUA).append("github.com/Lama06/Schneckenhaus\n").reset();
        builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Lama06/Schneckenhaus/"));
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Report issues, give feedback etc. here")));

        builder.append("Commands:").reset().bold(true).color(ChatColor.YELLOW);
        builder.append(" (Hover for information)").reset();

        record CommandHelp(String command, String description) { }
        final List<CommandHelp> commandHelpList = List.of(
                new CommandHelp("/sh help", "Shows this page"),
                new CommandHelp("/sh create", "Creates a snail shell with random size and color"),
                new CommandHelp("/sh create <size>", "Creates a snail shell with the specified size and random color"),
                new CommandHelp("/sh create <size> <color>", "Creates a snail shell with the specified size and color"),
                new CommandHelp("/sh info", "Shows information about the snail shell you are standing in"),
                new CommandHelp("/sh info <id>", "Shows information about the snail shell with the specified id"),
                new CommandHelp("/sh giveShulker", "Gives you a shulker box connected to the snail shell you are standing in"),
                new CommandHelp("/sh giveShulker <id>", "Gives you a shulker box connected to the snail shell with the specified id")
        );
        for (final CommandHelp commandHelp : commandHelpList) {
            builder.append("\n" + commandHelp.command()).reset().color(ChatColor.AQUA);
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandHelp.description())));
            builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandHelp.command()));
        }

        sender.spigot().sendMessage(builder.build());
    }

    private void create(final CommandSender sender, final String[] args) {
        final Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final int size;
        if (args.length >= 1) {
            Integer parsedSize = requireInteger(sender, args[0], SnailShell.MIN_SIZE, SnailShell.MAX_SIZE);
            if (parsedSize == null) {
                return;
            }
            size = parsedSize;
        } else {
            size = SnailShell.MIN_SIZE + rnd.nextInt(SnailShell.MAX_SIZE - SnailShell.MIN_SIZE + 1);
        }
        final DyeColor color;
        if (args.length >= 2) {
            color = Arrays.stream(DyeColor.values()).filter(c -> c.name().equalsIgnoreCase(args[1])).findAny().orElse(null);
            if (color == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Invalid color: " + args[1]).color(ChatColor.RED).build());
                return;
            }
        } else {
            final DyeColor[] dyeColors = DyeColor.values();
            color = dyeColors[rnd.nextInt(dyeColors.length)];
        }
        final int id = SchneckenPlugin.INSTANCE.getAndIncrementNextId();
        final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
        snailShell.create(size, color, player);
        player.teleport(snailShell.getPosition().getSpawnLocation());
        if (!player.getInventory().addItem(snailShell.createShulkerBox()).isEmpty()) {
            player.spigot().sendMessage(new ComponentBuilder("Your inventory is full").color(ChatColor.RED).build());
        }
    }

    private List<String> createTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return IntStream.range(SnailShell.MIN_SIZE, SnailShell.MAX_SIZE + 1).mapToObj(Integer::toString).toList();
        }
        return Arrays.stream(DyeColor.values()).map(Enum::name).map(String::toLowerCase).toList();
    }

    private void info(final CommandSender sender, final String[] args) {
        final SnailShell snailShell = requireSnailShell(sender, args.length == 1 ? args[0] : null);

        final ComponentBuilder builder = new ComponentBuilder();
        builder.append("Snail Shell\n").color(ChatColor.YELLOW).bold(true);
        for (final Map.Entry<String, String> property : getSnailShellInfoProperties(snailShell)) {
            builder.append(property.getKey() + ": ").reset().color(ChatColor.AQUA);
            builder.append(property.getValue() + "\n").reset();
            if (property.getKey().equals("Color")) {
                builder.color(ChatColor.of(new Color(snailShell.getColor().getColor().asRGB())));
            }
        }

        builder.append("> Teleport <\n").reset().color(ChatColor.LIGHT_PURPLE);
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
        final Location spawnLocation = snailShell.getPosition().getSpawnLocation();
        final String spawnLocationText = "%d %d %d".formatted(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
        final String tpCommand = "/execute in %s run tp %s".formatted(SchneckenPlugin.WORLD_NAME, spawnLocationText);
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand));

        builder.append("> Give Shulker Box <").reset().color(ChatColor.LIGHT_PURPLE);
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to get a shulker box connected to this snail shell")));
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sh giveShulker " + snailShell.getPosition().getId()));

        sender.spigot().sendMessage(builder.build());
    }

    private List<Map.Entry<String, String>> getSnailShellInfoProperties(final SnailShell snailShell) {
        final GridPosition position = snailShell.getPosition();
        final Block cornerBlock = position.getCornerBlock();
        final int size = snailShell.getSize();
        final String creatorName = snailShell.getCreator().getName();
        return List.of( // When editing certain keys, also edit them above
                Map.entry("Id", Integer.toString(position.getId())),
                Map.entry("Grid Position", "X: %d, Z: %d".formatted(position.getX(), position.getZ())),
                Map.entry("World Position", "X: %d, Z: %d".formatted(cornerBlock.getX(), cornerBlock.getZ())),
                Map.entry("Size", "%dx%d".formatted(size, size)),
                Map.entry("Creator", creatorName == null ? "Unknown" : creatorName),
                Map.entry("Color", snailShell.getColor().name().toLowerCase())
        );
    }

    private void giveShulker(final CommandSender sender, final String[] args) {
        final Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        final SnailShell snailShell = requireSnailShell(sender, args.length == 1 ? args[0] : null);
        if (snailShell == null) {
            return;
        }
        final ItemStack shulkerBox = snailShell.createShulkerBox();
        if (!player.getInventory().addItem(shulkerBox).isEmpty()) {
            player.spigot().sendMessage(new ComponentBuilder("Your inventory is full").color(ChatColor.RED).build());
        }
    }

    private void debugCreateShells(final CommandSender sender, final String[] args) {
        if (args.length != 1 || !args[0].equals("IKnowWhatIAmDoing")) {
            return;
        }
        final Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        final int COUNT = 100;
        final RandomGenerator rnd = ThreadLocalRandom.current();
        for (int i = 0; i < COUNT; i++) {
            final int finalI = i;
            Bukkit.getScheduler().runTaskLater(SchneckenPlugin.INSTANCE, () -> {
                final int id = SchneckenPlugin.INSTANCE.getAndIncrementNextId();
                final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
                final int size = SnailShell.MIN_SIZE + rnd.nextInt(SnailShell.MAX_SIZE - SnailShell.MIN_SIZE + 1);
                final DyeColor[] dyeColors = DyeColor.values();
                final DyeColor color = dyeColors[rnd.nextInt(dyeColors.length)];
                snailShell.create(size, color, player);
                player.spigot().sendMessage(new ComponentBuilder(Integer.toString(finalI + 1)).color(ChatColor.GREEN).build());
            }, i);
        }
    }

    private Player requirePlayer(final CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            sender.spigot().sendMessage(new ComponentBuilder().append("You are not a player").color(ChatColor.RED).build());
            return null;
        }
        return player;
    }

    private Integer requireInteger(final CommandSender sender, final String arg, final Integer minInclusive, final Integer maxInclusive) {
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

    private Integer requireInteger(final CommandSender sender, final String arg) {
        return requireInteger(sender, arg, null, null);
    }

    private SnailShell requireSnailShell(final CommandSender sender, final String arg) {
        final int id;
        if (arg == null) {
            final Player player = requirePlayer(sender);
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
            final Integer parsedId = requireInteger(sender, arg, 1, null);
            if (parsedId == null) {
                return null;
            }
            id = parsedId;
        }
        final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
        if (!snailShell.exists()) {
            sender.spigot().sendMessage(new ComponentBuilder("This snail shell doesn't exist").color(ChatColor.RED).build());
            return null;
        }
        return snailShell;
    }
}
