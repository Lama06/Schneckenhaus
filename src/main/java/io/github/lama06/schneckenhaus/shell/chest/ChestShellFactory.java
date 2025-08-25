package io.github.lama06.schneckenhaus.shell.chest;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellConfig;
import io.github.lama06.schneckenhaus.shell.sized.SizedShellFactory;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.WoodType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;

import java.util.Locale;
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
    public SizedShellConfig getConfig() {
        return SchneckenhausPlugin.INSTANCE.getPluginConfig().getChest();
    }

    @Override
    public ChestShellBuilder newBuilder() {
        return new ChestShellBuilder();
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
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("wood", new EnumArgumentType<>(WoodType.class));
    }

    @Override
    public boolean parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        if (!super.parseCommandParameters(builder, context, parameters)) {
            return false;
        }

        ChestShellBuilder chestBuilder = (ChestShellBuilder) builder;
        if (parameters.get("wood") instanceof WoodType wood) {
            chestBuilder.setWood(wood);
        } else {
            chestBuilder.setWood(EnumUtil.getRandom(WoodType.class));
        }

        return true;
    }

    @Override
    public boolean deserializeConfig(ShellBuilder builder, Map<?, ?> config) {
        if (!super.deserializeConfig(builder, config)) {
            return false;
        }
        ChestShellBuilder chestBuilder = (ChestShellBuilder) builder;
        if (config.get("wood") instanceof String wood) {
            try {
                chestBuilder.setWood(WoodType.valueOf(wood.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                logger.error("invalid wood type in chest shell config: {}", wood);
                chestBuilder.setWood(WoodType.OAK);
            }
        } else {
            chestBuilder.setWood(WoodType.OAK);
        }
        return true;
    }

    @Override
    public Material getItemType(ShellData data) {
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
