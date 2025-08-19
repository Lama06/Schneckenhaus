package io.github.lama06.schneckenhaus.command.custom;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.CustomShellTypeArgument;
import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class CustomCommand extends ConstantsHolder {
    private static final SimpleCommandExceptionType CUSTOM_NAME_TAKEN = new SimpleCommandExceptionType(Message.CUSTOM_SHELL_TYPE_NAME_TAKEN::toString);

    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("custom")
            .requires(Permission.COMMAND_CUSTOM::check)
            .then(new EditCustomCommand().create())
            .then(Commands.literal("add")
                .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("world", ArgumentTypes.world())
                        .then(Commands.argument("position1", ArgumentTypes.blockPosition())
                            .then(Commands.argument("position2", ArgumentTypes.blockPosition())
                                .then(Commands.argument("item", ArgumentTypes.resource(RegistryKey.ITEM))
                                    .then(Commands.argument("ingredient", ArgumentTypes.itemStack())
                                        .executes(this::add)
                                    )
                                )
                            )
                        )
                    )
                )
            )
            .then(Commands.literal("import")
                .then(Commands.argument("file", StringArgumentType.string())
                    .suggests((context, builder) -> {
                        try (Stream<Path> files = Files.list(plugin.getDataPath().resolve("import"))) {
                            files.forEach(path -> builder.suggest(path.getFileName().toString()));
                        } catch (IOException e) {
                            logger.error("failed to provide custom shell type import suggestions", e);
                        }
                        return builder.buildFuture();
                    })
                )
            )
            .then(Commands.literal("export")
                .then(Commands.argument("type", CustomShellTypeArgument.INSTANCE)
                )
            )
            .build();
    }

    private int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        if (config.getCustom().containsKey(name)) {
            throw CUSTOM_NAME_TAKEN.create();
        }
        World world = context.getArgument("world", World.class);
        BlockPosition position1 = new BlockPosition(context.getArgument("position1", BlockPositionResolver.class).resolve(context.getSource()));
        BlockPosition position2 = new BlockPosition(context.getArgument("position2", BlockPositionResolver.class).resolve(context.getSource()));
        ItemType item = context.getArgument("item", ItemType.class);
        ItemStack ingredient = context.getArgument("ingredient", ItemStack.class);

        CustomShellConfig config = new CustomShellConfig();
        config.setTemplateWorld(world.getName());
        config.setTemplatePosition(new BlockArea(position1, position2));
        config.setItem(Registry.MATERIAL.get(item.getKey()));
        config.getIngredients().add(new ItemConfig(ingredient));
        this.config.getCustom().put(name, config);
        plugin.getConfigManager().save();

        context.getSource().getSender().sendMessage(Message.ADD_CUSTOM_SHELL_TYPE_SUCCESS.asComponent(NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }
}
