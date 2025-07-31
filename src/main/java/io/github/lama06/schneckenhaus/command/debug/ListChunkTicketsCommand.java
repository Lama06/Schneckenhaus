package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public final class ListChunkTicketsCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<Chunk> chunks = SchneckenPlugin.INSTANCE.getWorld().getBukkit().getPluginChunkTickets().get(SchneckenPlugin.INSTANCE);
        if (chunks == null) {
            return;
        }
        TextComponent.Builder builder = Component.text();
        for (Chunk chunk : chunks) {
            builder.append(Component.text(chunk.getX() + " " + chunk.getZ())).appendNewline();
        }
        sender.sendMessage(builder);
    }
}
