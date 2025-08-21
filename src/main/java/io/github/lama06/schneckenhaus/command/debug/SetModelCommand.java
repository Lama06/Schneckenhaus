package io.github.lama06.schneckenhaus.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SetModelCommand {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("set-model")
            .then(Commands.argument("model", IntegerArgumentType.integer())
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player player)) {
                        return 0;
                    }
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(IntegerArgumentType.getInteger(context, "model"));
                    item.setItemMeta(meta);
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
    }
}
