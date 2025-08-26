package io.github.lama06.schneckenhaus.command.custom;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.command.CommandUtils;
import io.github.lama06.schneckenhaus.command.argument.CustomShellTypeArgument;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .executes(this::addInitialBlocks)
                    )
                )
                .then(Commands.literal("add-initial-block-area")
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .then(Commands.argument("position2", ArgumentTypes.blockPosition())
                            .executes(this::addInitialBlocks)
                        )
                    )
                )
                .then(Commands.literal("set-protect-air")
                    .then(Commands.argument("protectAir", BoolArgumentType.bool())
                        .executes(this::setProtectAir)
                    )
                )
                .then(Commands.literal("set-block-restrictions")
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .executes(this::setBlockRestrictions)
                        .then(Commands.argument("restriction1", ArgumentTypes.resource(RegistryKey.BLOCK))
                            .executes(this::setBlockRestrictions)
                            .then(Commands.argument("restriction2", ArgumentTypes.resource(RegistryKey.BLOCK))
                                .executes(this::setBlockRestrictions)
                                .then(Commands.argument("restriction3", ArgumentTypes.resource(RegistryKey.BLOCK))
                                    .executes(this::setBlockRestrictions)
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("set-block-area-restrictions")
                    .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                        .then(Commands.argument("position2", ArgumentTypes.blockPosition())
                            .executes(this::setBlockRestrictions)
                            .then(Commands.argument("restriction1", ArgumentTypes.resource(RegistryKey.BLOCK))
                                .executes(this::setBlockRestrictions)
                                .then(Commands.argument("restriction2", ArgumentTypes.resource(RegistryKey.BLOCK))
                                    .executes(this::setBlockRestrictions)
                                    .then(Commands.argument("restriction3", ArgumentTypes.resource(RegistryKey.BLOCK))
                                        .executes(this::setBlockRestrictions)
                                    )
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("clear-block-restrictions")
                    .executes(this::clearBlockRestrictions)
                )
                .then(Commands.literal("register-plants")
                    .then(Commands.argument("plant", new EnumArgumentType<>(PlantType.class))
                        .then(Commands.argument("obstruction", ArgumentTypes.resource(RegistryKey.BLOCK))
                            .executes(this::registerPlants)
                        )
                    )
                )
            )
            .build();
    }
    
    private CustomShellConfig getConfig(CommandContext<CommandSourceStack> context) {
        return config.getCustom().get(context.getArgument("type", String.class));
    }

    private int addIngredient(CommandContext<CommandSourceStack> context) {
        CustomShellConfig config = getConfig(context);
        ItemStack ingredient = context.getArgument("ingredient", ItemStack.class);
        config.getIngredients().add(new ItemConfig(ingredient));
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = getConfig(context);
        Player player = CommandUtils.requirePlayer(context.getSource());
        config.setSpawnPosition(player.getLocation());
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setMenuBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = getConfig(context);
        BlockPosition menuBlock = new BlockPosition(context.getArgument("menuBlock", BlockPositionResolver.class).resolve(context.getSource()));
        config.setMenuBlock(menuBlock);
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addExitBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = getConfig(context);
        BlockPosition exitBlock = new BlockPosition(context.getArgument("exitBlock", BlockPositionResolver.class).resolve(context.getSource()));
        config.getExitBlocks().add(exitBlock);
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int addInitialBlocks(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = getConfig(context);

        BlockArea area = getBlockAreaOrBlock(context);
        for (BlockPosition position : area) {
            if (position.getBlock(Bukkit.getWorld(config.getTemplateWorld())).isEmpty()) {
                continue;
            }
            config.getBlockRestrictions().put(position, Set.of());
        }

        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setProtectAir(CommandContext<CommandSourceStack> context) {
        getConfig(context).setProtectAir(BoolArgumentType.getBool(context, "protectAir"));
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int setBlockRestrictions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CustomShellConfig config = getConfig(context);

        BlockArea area = getBlockAreaOrBlock(context);

        Set<Material> restrictions = new HashSet<>();
        for (int i = 1; i <= 3; i++) {
            BlockType restriction = CommandUtils.getArgumentOrDefault(context, "restriction" + i, BlockType.class, null);
            if (restriction != null) {
                restrictions.add(Registry.MATERIAL.get(restriction.getKey()));
            }
        }

        for (BlockPosition position : area) {
            config.getBlockRestrictions().put(position, new HashSet<>(restrictions));
        }

        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private BlockArea getBlockAreaOrBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPosition position1 = new BlockPosition(context.getArgument("position1", BlockPositionResolver.class).resolve(context.getSource()));

        BlockPositionResolver position2Resolver = CommandUtils.getArgumentOrDefault(context, "position2", BlockPositionResolver.class, null);
        BlockPosition position2;
        if (position2Resolver == null) {
            position2 = position1;
        } else {
            position2 = new BlockPosition(position2Resolver.resolve(context.getSource()));
        }

        return new BlockArea(position1, position2);
    }

    private int registerPlants(CommandContext<CommandSourceStack> context) {
        CustomShellConfig config = getConfig(context);
        PlantType plant = context.getArgument("plant", PlantType.class);
        Material growObstruction = Registry.MATERIAL.get(context.getArgument("obstruction", BlockType.class).getKey());

        HashSet<Material> plantMaterialsAndAir = new HashSet<>(plant.materials);
        plantMaterialsAndAir.add(Material.AIR);

        List<Block> roots = new ArrayList<>();
        World world = Bukkit.getWorld(config.getTemplateWorld());
        for (BlockPosition position : config.getTemplatePosition()) {
            Block root = position.getBlock(world);
            if (!plant.materials.contains(root.getType())) {
                continue;
            }
            if (plant.materials.contains(root.getRelative(0, -plant.growDirection, 0).getType())) {
                continue;
            }
            roots.add(root);
        }

        for (Block root : roots) {
            config.getBlockRestrictions().put(new BlockPosition(root), plant.materials);

            Block firstGrowBlock = root.getRelative(0, plant.growDirection, 0);
            if (!plant.materials.contains(firstGrowBlock.getType()) && !firstGrowBlock.isEmpty() && firstGrowBlock.getType() != growObstruction) {
                continue;
            }
            firstGrowBlock.setType(growObstruction);
            config.getBlockRestrictions().put(new BlockPosition(firstGrowBlock), plantMaterialsAndAir);

            for (int i = 2;; i++) {
                Block growBlock = root.getRelative(0, i * plant.growDirection, 0);
                if (!plant.materials.contains(growBlock.getType()) && !growBlock.isEmpty()) {
                    break;
                }
                config.getBlockRestrictions().put(new BlockPosition(growBlock), plantMaterialsAndAir);
            }
        }

        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }

    private int clearBlockRestrictions(CommandContext<CommandSourceStack> context) {
        CustomShellConfig config = getConfig(context);
        config.getBlockRestrictions().clear();
        plugin.getConfigManager().save();
        return Command.SINGLE_SUCCESS;
    }
}
