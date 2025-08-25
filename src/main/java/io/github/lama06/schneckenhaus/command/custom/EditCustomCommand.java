package io.github.lama06.schneckenhaus.command.custom;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.command.CommandUtils;
import io.github.lama06.schneckenhaus.command.argument.CustomShellTypeArgument;
import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class EditCustomCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("edit")
            .then(Commands.argument("type", CustomShellTypeArgument.INSTANCE)
                .then(Commands.literal("add-ingredient")
                    .then(Commands.argument("ingredient", ArgumentTypes.itemStack())
                        .executes(this::addIngredient)
                    )
                )
                .then(Commands.literal("set-spawn")
                    .executes(this::setSpawn)
                )
                .then(Commands.literal("set-menu-block")
                    .then(Commands.argument("menuBlock", ArgumentTypes.blockPosition())
                        .executes(this::setMenuBlock)
                    )
                )
                .then(Commands.literal("add-exit-block")
                    .then(Commands.argument("exitBlock", ArgumentTypes.blockPosition())
                        .executes(this::addExitBlock)
                    )
                )
                .then(Commands.literal("add-initial-block")
                    .then(Commands.argument("initialBlock", ArgumentTypes.blockPosition())
                        .executes(this::addInitialBlock)
                    )
                )
                .then(Commands.literal("add-initial-block-area")
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .then(Commands.argument("position2", ArgumentTypes.blockPosition())
                            .executes(this::addInitialBlockArea)
                        )
                    )
                )
                .then(Commands.literal("add-alternative-block")
                    .then(Commands.argument("block", ArgumentTypes.blockPosition())
                        .then(Commands.argument("alternative", ArgumentTypes.resource(RegistryKey.BLOCK))
                            .executes(this::addAlternativeBlock)
                        )
                    )
                )
                .then(Commands.literal("add-alternative-block-area")
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .then(Commands.argument("position2", ArgumentTypes.blockPosition())
                            .then(Commands.argument("alternative", ArgumentTypes.resource(RegistryKey.BLOCK))
                                .executes(this::addAlternativeBlockArea)
                            )
                        )
                    )
                )
            )
            .build();
    }

    private int addIngredient(CommandContext<CommandSourceStack> context) {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        ItemStack ingredient = context.getArgument("ingredient", ItemStack.class);
        config.getIngredients().add(new ItemConfig(ingredient));
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        Player player = CommandUtils.requirePlayer(context.getSource());
        config.setSpawnPosition(player.getLocation());
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setMenuBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition menuBlock = new BlockPosition(context.getArgument("menuBlock", BlockPositionResolver.class).resolve(context.getSource()));
        config.setMenuBlock(menuBlock);
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addExitBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition exitBlock = new BlockPosition(context.getArgument("exitBlock", BlockPositionResolver.class).resolve(context.getSource()));
        config.getExitBlocks().add(exitBlock);
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addInitialBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition initialBlock = new BlockPosition(context.getArgument("initialBlock", BlockPositionResolver.class).resolve(context.getSource()));
        config.getInitialBlocks().add(initialBlock);
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addInitialBlockArea(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition position1 = new BlockPosition(context.getArgument("position1", BlockPositionResolver.class).resolve(context.getSource()));
        BlockPosition position2 = new BlockPosition(context.getArgument("position2", BlockPositionResolver.class).resolve(context.getSource()));
        BlockArea area = new BlockArea(position1, position2);
        for (BlockPosition position : area) {
            if (position.getBlock(Bukkit.getWorld(config.getTemplateWorld())).isEmpty()) {
                continue;
            }
            config.getInitialBlocks().add(position);
        }
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addAlternativeBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition block = new BlockPosition(context.getArgument("block", BlockPositionResolver.class).resolve(context.getSource()));
        BlockType alternative = context.getArgument("alternative", BlockType.class);
        Set<Material> materials = config.getAlternativeBlocks().computeIfAbsent(block, key -> new HashSet<>());
        materials.add(Registry.MATERIAL.get(alternative.getKey()));
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addAlternativeBlockArea(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = this.config.getCustom().get(context.getArgument("type", String.class));
        BlockPosition position1 = new BlockPosition(context.getArgument("position1", BlockPositionResolver.class).resolve(context.getSource()));
        BlockPosition position2 = new BlockPosition(context.getArgument("position2", BlockPositionResolver.class).resolve(context.getSource()));
        BlockType alternative = context.getArgument("alternative", BlockType.class);
        for (BlockPosition position : new BlockArea(position1, position2)) {
            Set<Material> materials = config.getAlternativeBlocks().computeIfAbsent(position, key -> new HashSet<>());
            materials.add(Registry.MATERIAL.get(alternative.getKey()));
        }
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }
}
