package io.github.lama06.schneckenhaus.shell.custom;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.parameter.CommandParameter;
import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public final class CustomShellFactory extends ShellFactory {
    public static final CustomShellFactory INSTANCE = new CustomShellFactory();

    private CustomShellFactory() { }

    @Override
    public String getName() {
        return "custom";
    }

    @Override
    public ShellBuilder newBuilder() {
        return new CustomShellBuilder();
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        CustomShellBuilder customBuilder = (CustomShellBuilder) builder;
        Map<String, GlobalCustomShellConfig> customConfigs = SchneckenPlugin.INSTANCE.getPluginConfig().getCustom();

        customConfigs:
        for (String name : customConfigs.keySet()) {
            GlobalCustomShellConfig customConfig = customConfigs.get(name);
            if (!customConfig.isCrafting()) {
                continue;
            }
            CraftingInput inputCopy = input.copy();
            if (!inputCopy.remove(customConfig.getItem())) {
                continue;
            }
            for (ItemConfig ingredient : customConfig.getIngredients()) {
                if (!inputCopy.remove(ingredient)) {
                    continue customConfigs;
                }
            }
            customBuilder.setTemplate(name);
            return true;
        }

        return false;
    }

    @Override
    protected void addCommandParameters(List<CommandParameter> parameters) {
        super.addCommandParameters(parameters);
        parameters.add(CommandParameter.required("template", StringArgumentType.string()));
    }

    @Override
    public CustomShellBuilder parseCommandParameters(
        CommandContext<CommandSourceStack> context,
        Map<String, ?> parameters
    ) throws CommandSyntaxException {
        CustomShellBuilder builder = (CustomShellBuilder) super.parseCommandParameters(context, parameters);
        builder.setTemplate((String) parameters.get("template"));
        return builder;
    }

    @Override
    public ShellBuilder deserializeConfig(Map<?, ?> config) {
        CustomShellBuilder builder = new CustomShellBuilder();
        if (!(config.get("template") instanceof String template)) {
            return null;
        }
        builder.setTemplate(template);
        return builder;
    }

    @Override
    protected Material getItemType(ShellData data) {
        CustomShellData customData = (CustomShellData) data;
        return SchneckenPlugin.INSTANCE.getPluginConfig().getCustom().get(customData.getTemplate()).getItem();
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
