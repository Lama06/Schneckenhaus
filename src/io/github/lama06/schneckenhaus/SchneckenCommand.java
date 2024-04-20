package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
            case "create" -> create(sender, remainingArgs);
            case "info" -> info(sender, remainingArgs);
            default -> sender.spigot().sendMessage(new ComponentBuilder("Invalid command").color(ChatColor.RED).build());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("create", "info");
        }
        final String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        return switch (args[0]) {
            case "create" -> createTabComplete(sender, remainingArgs);
            default -> null;
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

        builder.append("Commands:\n").reset().bold(true).color(ChatColor.YELLOW);

        builder.append("/sh create [size] [color]\n").reset();
        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sh create"));
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Creates a new snail shell and gives it to you")));

        builder.append("/sh info [id]").reset();
        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sh info"));
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Shows information about a snail shell")));

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

        final ItemStack item = new ItemStack(MaterialUtil.getColoredShulkerBox(color));
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Snail Shell");
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        itemData.set(Data.SHULKER_ITEM_ID, PersistentDataType.INTEGER, id);
        item.setItemMeta(meta);
        if (!player.getInventory().addItem(item).isEmpty()) {
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
        final int id;
        if (args.length == 0) {
            final Player player = requirePlayer(sender);
            if (player == null) {
                return;
            }
            final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
            if (position == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Move to a snail shell or enter an id").color(ChatColor.RED).build());
                return;
            }
            id = position.getId();
        } else {
            final Integer parsedId = requireInteger(sender, args[0], 1, null);
            if (parsedId == null) {
                return;
            }
            id = parsedId;
        }
        final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
        if (!snailShell.exists()) {
            sender.spigot().sendMessage(new ComponentBuilder("This snail shell doesn't exist").color(ChatColor.RED).build());
            return;
        }

        final ComponentBuilder builder = new ComponentBuilder();
        builder.color(ChatColor.YELLOW).append("Snail Shell").bold(true);

        for (final Map.Entry<String, String> property : getSnailShellInfoProperties(snailShell)) {
            builder.append("\n" + property.getKey() + ": ").reset().color(ChatColor.AQUA);
            if (property.getKey().contains("Position")) {
                builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
                final Location spawnLocation = snailShell.getPosition().getSpawnLocation();
                final String spawnLocationText = "%d %d %d".formatted(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
                final String command = "/execute in %s run tp %s".formatted(SchneckenPlugin.WORLD_NAME, spawnLocationText);
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            }
            builder.append(property.getValue()).color(ChatColor.WHITE);
            if (property.getKey().equals("Color")) {
                builder.color(ChatColor.of(new Color(snailShell.getColor().getColor().asRGB())));
            }
        }

        sender.spigot().sendMessage(builder.build());
    }

    private static List<Map.Entry<String, String>> getSnailShellInfoProperties(final SnailShell snailShell) {
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
}
