package io.github.lama06.schneckenhaus.shell.sized;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.command.parameter.CommandParameter;
import io.github.lama06.schneckenhaus.position.Position;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Map;

public abstract class SizedShellFactory extends BuiltinShellFactory {
    public abstract int getMinSize();

    public int getMaxSize() {
        return Position.CELL_SIZE - 2;
    }

    public abstract GlobalSizedShellConfig getGlobalConfig();

    @Override
    public abstract SizedShellBuilder newBuilder();

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }
        SizedShellBuilder sizedBuilder = (SizedShellBuilder) builder;

        GlobalSizedShellConfig config = getGlobalConfig();

        int sizeIngredients = 0;
        while (input.remove(config.getSizeIngredient())) {
            sizeIngredients++;
        }
        int size = config.getInitialCraftingSize() + sizeIngredients * config.getSizePerIngredient();
        size = Math.min(size, config.getMaxCraftingSize());
        size = Math.clamp(size, getMinSize(), getMaxSize());
        sizedBuilder.setSize(size);

        return true;
    }

    @Override
    protected void addCommandParameters(List<CommandParameter> parameters) {
        super.addCommandParameters(parameters);
        parameters.add(CommandParameter.optional("size", IntegerArgumentType.integer(getMinSize(), getMaxSize())));
    }

    @Override
    public SizedShellBuilder parseCommandParameters(
        CommandContext<CommandSourceStack> context,
        Map<String, ?> parameters
    ) throws CommandSyntaxException {
        SizedShellBuilder builder = (SizedShellBuilder) super.parseCommandParameters(context, parameters);
        if (parameters.get("size") instanceof Integer size) {
            builder.setSize(size);
        }
        return builder;
    }

    @Override
    public ShellBuilder deserializeConfig(Map<?, ?> config) {
        SizedShellBuilder builder = (SizedShellBuilder) super.deserializeConfig(config);
        if (config.get("size") instanceof Integer size) {
            builder.setSize(size);
        } else {
            builder.setSize(Math.clamp(16, getMinSize(), getMaxSize()));
        }
        return builder;
    }

    @Override
    protected List<Component> getItemLore(ShellData data) {
        SizedShellData sizedData = (SizedShellData) data;
        int size = sizedData.getSize();
        return List.of(Component.text(size + "x" + size).decoration(TextDecoration.ITALIC, false));
    }
}
