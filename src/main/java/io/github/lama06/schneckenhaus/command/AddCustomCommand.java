package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.util.BlockArea;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AddCustomCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("<name> <template area> <ingredients>", "Adds a new custom snail shell type."));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 7) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough arguments").color(ChatColor.RED).build());
            return;
        }
        final String name = args[0];
        final BlockArea template = Require.blockArea(sender, Arrays.copyOfRange(args, 1, 1 + 6));
        if (template == null) {
            return;
        }
        final List<Material> ingredients = new ArrayList<>();
        for (final String ingredientArg : Arrays.copyOfRange(args, 7, args.length)) {
            final Material ingredient = Require.material(sender, ingredientArg);
            if (ingredient == null) {
                return;
            }
            ingredients.add(ingredient);
        }

        final ConfigurationSection customConfig = SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("custom");
        final ConfigurationSection section = customConfig.createSection(name);
        section.set("enabled", true);
        section.set("template", template.toString());
        section.set("ingredients", ingredients.stream().map(Keyed::getKey).map(Object::toString).toList());

        SchneckenPlugin.INSTANCE.getRecipeManager().registerRecipes();

        sender.spigot().sendMessage(new ComponentBuilder("Successfully added shell").color(ChatColor.GREEN).build());
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return List.of();
        }
        if (args.length <= 7) {
            return TabComplete.blockArea(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return TabComplete.material(args[args.length-1]);
    }
}
