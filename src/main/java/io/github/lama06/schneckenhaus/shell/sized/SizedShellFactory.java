package io.github.lama06.schneckenhaus.shell.sized;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.shell.position.ShellPosition;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class SizedShellFactory extends BuiltinShellFactory {
    public abstract int getMinSize();

    public int getMaxSize() {
        return ShellPosition.CELL_SIZE - 2;
    }

    @Override
    public abstract SizedShellConfig getConfig();

    @Override
    public abstract SizedShellBuilder newBuilder();

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }
        SizedShellBuilder sizedBuilder = (SizedShellBuilder) builder;

        SizedShellConfig config = getConfig();

        int sizeIngredients = 0;
        while (input.remove(config.getSizeIngredient())) {
            sizeIngredients++;
        }
        int size = config.getInitialCraftingSize() + sizeIngredients * config.getSizePerIngredient();
        size = Math.min(size, config.getMaxCraftingSize());
        sizedBuilder.setSize(size);

        return true;
    }

    @Override
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("size", IntegerArgumentType.integer(getMinSize(), getMaxSize()));
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
        SizedShellBuilder sizedBuilder = (SizedShellBuilder) builder;
        if (parameters.get("size") instanceof Integer size) {
            sizedBuilder.setSize(size);
        } else {
            sizedBuilder.setSize(getMinSize() + ThreadLocalRandom.current().nextInt(Math.min(getMaxSize(), 32) - getMinSize() + 1));
        }
        return true;
    }

    @Override
    public boolean deserializeConfig(ShellBuilder builder, Map<?, ?> config) {
        if (!super.deserializeConfig(builder, config)) {
            return false;
        }
        SizedShellBuilder sizedBuilder = (SizedShellBuilder) builder;
        if (config.get("size") instanceof Integer size) {
            sizedBuilder.setSize(size);
        } else {
            sizedBuilder.setSize(Math.clamp(16, getMinSize(), getMaxSize()));
        }
        return true;
    }

    @Override
    protected List<Component> getItemLore(ShellData data) {
        SizedShellData sizedData = (SizedShellData) data;
        int size = sizedData.getSize();
        return List.of(Component.text(size + "x" + size).decoration(TextDecoration.ITALIC, false));
    }
}
