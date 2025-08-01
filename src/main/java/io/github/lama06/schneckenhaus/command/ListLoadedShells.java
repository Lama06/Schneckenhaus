package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListLoadedShells extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<Chunk> chunks = SchneckenPlugin.INSTANCE.getWorld().getBukkit().getPluginChunkTickets().get(SchneckenPlugin.INSTANCE);
        if (chunks == null) {
            sender.sendMessage(Component.text("No snail shells are loaded"));
            return;
        }
        Set<Integer> shells = new HashSet<>();
        for (Chunk chunk : chunks) {
            CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(chunk.getBlock(0, 0, 0).getLocation());
            if (position == null) {
                continue;
            }
            shells.add(position.getId());
        }
        TextComponent.Builder builder = Component.text()
            .append(
                Component.text("IDs of loaded snail shells: (Click for more information)", NamedTextColor.YELLOW)
            )
            .appendNewline();
        for (int shell : shells) {
            builder.append(
                Component.text(shell)
                    .clickEvent(ClickEvent.runCommand("/sh info " + shell))
                    .hoverEvent(HoverEvent.showText(Component.text("Click for more information")))
            );
            builder.append(Component.space());
        }
        sender.sendMessage(builder);
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
            new HelpCommand.Entry("", "Get a list of loaded snail shells")
        );
    }
}
