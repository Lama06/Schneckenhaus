package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.command.Command;
import io.github.lama06.schneckenhaus.command.Require;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class ViewDataCommand extends Command {
    private final List<PersistentDataType<?, ?>> PERSISTENT_DATA_TYPES = List.of(
            PersistentDataType.BYTE,
            PersistentDataType.SHORT,
            PersistentDataType.INTEGER,
            PersistentDataType.LONG,
            PersistentDataType.FLOAT,
            PersistentDataType.DOUBLE,
            PersistentDataType.STRING
    );

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (args.length == 0) {
            return;
        }
        final PersistentDataContainer data = switch (args[0]) {
            case "chunk" -> player.getLocation().getChunk().getPersistentDataContainer();
            case "world" -> player.getWorld().getPersistentDataContainer();
            default -> null;
        };
        if (data == null) {
            return;
        }
        final TextComponent.Builder builder = Component.text();
        keys:
        for (final NamespacedKey key : data.getKeys()) {
            builder.appendNewline();
            builder.append(Component.text(key + ": ", NamedTextColor.YELLOW));
            for (final PersistentDataType<?, ?> type : PERSISTENT_DATA_TYPES) {
                try {
                    builder.append(Component.text(data.get(key, type).toString()));
                    continue keys;
                } catch (final IllegalArgumentException ignored) { }
            }
            builder.append(Component.text("Unknown Type", NamedTextColor.RED));
        }
        sender.sendMessage(builder);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return List.of("chunk", "world");
    }
}
