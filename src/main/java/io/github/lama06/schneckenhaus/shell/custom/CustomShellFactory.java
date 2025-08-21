package io.github.lama06.schneckenhaus.shell.custom;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.command.argument.CustomShellTypeArgument;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommandBuilder;
import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.Map;

public final class CustomShellFactory extends ShellFactory {
    public static final CustomShellFactory INSTANCE = new CustomShellFactory();

    private CustomShellFactory() { }

    @Override
    public String getId() {
        return "custom";
    }

    @Override
    public Message getName() {
        return Message.CUSTOM;
    }

    @Override
    public ShellBuilder newBuilder() {
        return new CustomShellBuilder();
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        CustomShellBuilder customBuilder = (CustomShellBuilder) builder;

        customConfigs:
        for (String name : config.getCustom().keySet()) {
            CustomShellConfig customConfig = config.getCustom().get(name);
            if (!customConfig.isCrafting()) {
                continue;
            }
            CraftingInput inputCopy = input.copy();
            for (ItemConfig ingredient : customConfig.getIngredients()) {
                if (!inputCopy.remove(ingredient)) {
                    continue customConfigs;
                }
            }
            input.remove(customConfig.getItem());
            for (ItemConfig ingredient : customConfig.getIngredients()) {
                input.remove(ingredient);
            }
            customBuilder.setTemplate(name);
            return true;
        }

        return false;
    }

    @Override
    public void addCommandParameters(ParameterCommandBuilder builder) {
        super.addCommandParameters(builder);
        builder.parameter("template", CustomShellTypeArgument.INSTANCE);
    }

    @Override
    public boolean parseCommandParameters(
        ShellBuilder builder,
        CommandContext<CommandSourceStack> context,
        Map<String, Object> parameters
    ) throws CommandSyntaxException {
        super.parseCommandParameters(builder, context, parameters);

        CustomShellBuilder customBuilder = (CustomShellBuilder) builder;
        if (!(parameters.get("template") instanceof String template)) {
            context.getSource().getSender().sendMessage(Message.MISSING_CUSTOM_SHELL_TYPE.asComponent(NamedTextColor.RED));
            return false;
        }

        if (!config.getCustom().containsKey(template)) {
            context.getSource().getSender().sendMessage(Message.INVALID_CUSTOM_SHELL_TYPE.asComponent(NamedTextColor.RED, template));
            return false;
        }

        customBuilder.setTemplate(template);
        return true;
    }

    @Override
    public boolean deserializeConfig(ShellBuilder builder, Map<?, ?> config) {
        CustomShellBuilder customBuilder = (CustomShellBuilder) builder;
        if (!(config.get("template") instanceof String template)) {
            logger.error("missing template in custom shell config");
            return false;
        }
        if (!this.config.getCustom().containsKey(template)) {
            logger.error("invalid template in custom shell config: {}", template);
            return false;
        }
        customBuilder.setTemplate(template);
        return true;
    }

    @Override
    protected Material getItemType(ShellData data) {
        CustomShellData customData = (CustomShellData) data;
        return config.getCustom().get(customData.getTemplate()).getItem();
    }

    @Override
    public Shell loadShell(int id) {
        CustomShell shell = new CustomShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}
