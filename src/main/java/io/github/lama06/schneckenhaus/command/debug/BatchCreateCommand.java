package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellCreationType;
import io.github.lama06.schneckenhaus.shell.ShellFactories;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellFactory;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class BatchCreateCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("batch-create")
            .executes(context -> execute(context, false, false))
            .then(Commands.argument("batches", IntegerArgumentType.integer())
                .executes(context -> execute(context, true, false))
                .then(Commands.argument("perBatch", IntegerArgumentType.integer())
                    .executes(context -> execute(context, true, true))
                )
            )
            .build();
    }

    private int execute(
        CommandContext<CommandSourceStack> context,
        boolean customBatches,
        boolean customPerBatch
    ) throws CommandSyntaxException {
        if (!(context.getSource().getSender() instanceof Player player)) {
            return 0;
        }
        int batches = 100;
        if (customBatches) {
            batches = IntegerArgumentType.getInteger(context, "batches");
        }
        final int perBatch;
        if (customPerBatch) {
            perBatch = IntegerArgumentType.getInteger(context, "perBatch");
        } else {
            perBatch = 10;
        }
        List<ShellFactory> factories = ShellFactories.getFactories().stream()
            .filter(factory -> !(factory instanceof CustomShellFactory))
            .toList();
        for (int batch = 0; batch < batches; batch++) {
            int finalBatch = batch;
            Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    player.sendMessage(Component.text(finalBatch, NamedTextColor.GREEN));
                    for (int i = 0; i < perBatch; i++) {
                        ShellFactory factory = factories.get(ThreadLocalRandom.current().nextInt(factories.size()));
                        ShellBuilder builder = factory.newBuilder();
                        builder.setCreator(player.getUniqueId());
                        builder.setCreationType(ShellCreationType.COMMAND);
                        try {
                            factory.parseCommandParameters(builder, context, Map.of());
                        } catch (CommandSyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        builder.build();
                    }
                },
                batch
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
