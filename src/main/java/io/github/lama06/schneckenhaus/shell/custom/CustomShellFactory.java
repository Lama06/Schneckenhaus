package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CustomShellFactory extends ShellFactory<CustomShellConfig> {
    public static final CustomShellFactory INSTANCE = new CustomShellFactory();

    private CustomShellFactory() { }

    @Override
    public String getName() {
        return "custom";
    }

    @Override
    public List<ShellRecipe<CustomShellConfig>> getRecipes() {
        final List<ShellRecipe<CustomShellConfig>> recipes = new ArrayList<>();
        final Map<String, CustomShellGlobalConfig> config = SchneckenPlugin.INSTANCE.getSchneckenConfig().custom;
        for (final String templateName : config.keySet()) {
            final CustomShellGlobalConfig templateConfig = config.get(templateName);
            if (!templateConfig.enabled) {
                continue;
            }
            final List<Material> ingredients = new ArrayList<>(templateConfig.ingredients);
            ingredients.add(Material.CHEST);
            recipes.add(new ShellRecipe<>(templateName, ingredients, new CustomShellConfig(templateName)));
        }
        return recipes;
    }

    @Override
    public CustomShell instantiate(final GridPosition position, final CustomShellConfig config) {
        return new CustomShell(position, config);
    }

    @Override
    public CustomShellConfig loadConfig(final PersistentDataContainer data) {
        final String name = CustomShellConfig.NAME.get(data);
        return new CustomShellConfig(name);
    }

    @Override
    public List<String> tabCompleteConfig(final CommandSender sender, final String[] args) {
        final Set<String> names = SchneckenPlugin.INSTANCE.getSchneckenConfig().custom.keySet();
        return new ArrayList<>(names);
    }

    @Override
    public CustomShellConfig parseConfig(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Missing template name", NamedTextColor.RED));
            return null;
        }
        final String template = args[0];
        return new CustomShellConfig(template);
    }

    @Override
    public List<String> getConfigCommandTemplates() {
        return List.of("<name>");
    }
}
