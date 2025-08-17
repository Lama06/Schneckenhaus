package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.position.Position;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class LoadingCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("loading")
            .then(Commands.literal("reasons")
                .executes(context -> {
                    context.getSource().getSender().sendMessage(plugin.getSystems().getLoadingSystem().getDebugMessage());
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("shells")
                .executes(context -> {
                    Set<Integer> positions = new HashSet<>();
                    for (World world : plugin.getShellManager().getShellWorlds()) {
                        Collection<Chunk> tickets = world.getPluginChunkTickets().get(SchneckenhausPlugin.INSTANCE);
                        if (tickets == null) {
                            continue;
                        }
                        for (Chunk chunk : tickets) {
                            Position position = Position.location(chunk.getBlock(1, 1, 1).getLocation());
                            if (position == null) {
                                continue;
                            }
                            positions.add(position.getId());
                        }
                    }
                    context.getSource().getSender().sendMessage(Component.text(positions.toString()));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
    }
}
