package io.github.lama06.schneckenhaus.shell.head;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.github.lama06.schneckenhaus.shell.builtin.GlobalBuiltinShellConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

public final class HeadShellFactory extends BuiltinShellFactory {
    public static final HeadShellFactory INSTANCE = new HeadShellFactory();

    @Override
    public GlobalBuiltinShellConfig getGlobalConfig() {
        return config.getHead();
    }

    @Override
    public String getId() {
        return "head";
    }

    @Override
    public Message getName() {
        return Message.HEAD;
    }

    @Override
    public ShellBuilder newBuilder() {
        return new HeadShellBuilder();
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }
        HeadShellBuilder headBuilder = (HeadShellBuilder) builder;
        headBuilder.setHeadOwner(builder.getCreator());
        return true;
    }

    @Override
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("owner", ArgumentTypes.playerProfiles());
    }

    @Override
    public void parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        super.parseCommandParameters(builder, context, parameters);

        HeadShellBuilder headBuilder = (HeadShellBuilder) builder;
        if (parameters.get("owner") instanceof PlayerProfileListResolver playerResolver) {
            PlayerProfile player = playerResolver.resolve(context.getSource()).iterator().next();
            headBuilder.setHeadOwner(player.getId());
        }
    }

    @Override
    protected Material getItemType(ShellData data) {
        return Material.PLAYER_HEAD;
    }

    @Override
    public ItemStack createItem(ShellData data) {
        ItemStack item = super.createItem(data);
        item.editMeta(SkullMeta.class, meta -> {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(data.getCreator()));
        });
        return item;
    }

    @Override
    public HeadShell loadShell(int id) {
        HeadShell shell = new HeadShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}
