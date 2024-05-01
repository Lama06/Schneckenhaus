package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellRecipe;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;
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
        final ConfigurationSection config = getGlobalConfig();
        for (final String templateName : config.getKeys(false)) {
            final ConfigurationSection templateConfig = config.getConfigurationSection(templateName);
            if (!templateConfig.getBoolean("enabled")) {
                continue;
            }
            final List<Material> ingredients = new ArrayList<>();
            ingredients.add(Material.CHEST);
            templateConfig.getStringList("ingredients").stream().map(Registry.MATERIAL::match).forEach(ingredients::add);
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
        final Set<String> names = SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("custom").getKeys(false);
        return new ArrayList<>(names);
    }

    @Override
    public CustomShellConfig parseConfig(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            sender.spigot().sendMessage(new ComponentBuilder("Missing template name").color(ChatColor.RED).build());
            return null;
        }
        final String template = args[0];
        return new CustomShellConfig(template);
    }

    @Override
    public List<String> getConfigCommandTemplates() {
        return List.of("<name>");
    }

    private ConfigurationSection getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("custom");
    }
}
