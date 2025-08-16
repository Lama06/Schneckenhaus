package io.github.lama06.schneckenhaus.shell.chest;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.GlobalSizedShellConfig;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellFactory;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.WoodType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;

import java.util.Map;

public final class ChestShellFactory extends SizedShellFactory {
    public static final ChestShellFactory INSTANCE = new ChestShellFactory();

    private ChestShellFactory() { }

    @Override
    public String getId() {
        return "chest";
    }

    @Override
    public Message getName() {
        return Message.CHEST;
    }

    @Override
    public int getMinSize() {
        return 4;
    }

    @Override
    public GlobalSizedShellConfig getGlobalConfig() {
        return SchneckenhausPlugin.INSTANCE.getPluginConfig().getChest();
    }

    @Override
    public ChestShellBuilder newBuilder() {
        return new ChestShellBuilder();
    }

    @Override
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("wood", new EnumArgumentType<>(WoodType.class));
    }

    @Override
    public void parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        super.parseCommandParameters(builder, context, parameters);

        ChestShellBuilder chestBuilder = (ChestShellBuilder) builder;
        if (parameters.get("wood") instanceof WoodType wood) {
            chestBuilder.setWood(wood);
        } else {
            chestBuilder.setWood(EnumUtil.getRandom(WoodType.class));
        }
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }
        ChestShellBuilder chestBuilder = (ChestShellBuilder) builder;
        chestBuilder.setWood(WoodType.OAK);
        return true;
    }

    @Override
    protected Material getItemType(ShellData data) {
        return Material.CHEST;
    }

    @Override
    public ChestShell loadShell(int id) {
        ChestShell shell = new ChestShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}
