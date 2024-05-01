package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NewShellTypeCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(new HelpCommand.Entry("<name> <template area> <ingredients>", "Adds a new custom snail shell type."));
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 8) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough arguments").color(ChatColor.RED).build());
            return;
        }
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (!player.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            sender.spigot().sendMessage(new ComponentBuilder("Templates need to be in the snail shell world").color(ChatColor.RED).build());
            return;
        }
        final ConfigurationSection customConfig = SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection("custom");
        final String name = args[0];
        if (customConfig.contains(name)) {
            sender.spigot().sendMessage(new ComponentBuilder("This name is already taken: " + name).color(ChatColor.RED).build());
            return;
        }
        final BlockArea template = Require.blockArea(sender, Arrays.copyOfRange(args, 1, 1 + 6));
        if (template == null) {
            return;
        }
        final BlockPosition upperCorner = template.getUpperCorner();
        if (upperCorner.x() >= 0 && upperCorner.z() >= 0) {
            final String error = "You should only build templates at x < 0 or z < 0.";
            sender.spigot().sendMessage(new ComponentBuilder(error).color(ChatColor.RED).build());
            return;
        }
        if (template.getWidthX() > GridPosition.CELL_SIZE || template.getWidthZ() > GridPosition.CELL_SIZE) {
            final String error = "Your template is too larger than %dx%d blocks"
                    .formatted(GridPosition.CELL_SIZE, GridPosition.CELL_SIZE);
            sender.spigot().sendMessage(new ComponentBuilder(error).color(ChatColor.RED).build());
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

        final ConfigurationSection section = customConfig.createSection(name);
        section.set("enabled", true);
        section.set("template", template.toString());
        section.set("ingredients", ingredients.stream().map(Keyed::getKey).map(Object::toString).toList());

        SchneckenPlugin.INSTANCE.getRecipeManager().registerRecipes();

        final ComponentBuilder builder = new ComponentBuilder();
        builder.append("Successfully added shell!").color(ChatColor.GREEN);
        builder.append("\nYou can edit it in the config.").reset();
        builder.append(" Remember that you need a chest besides the specified ingredients to craft it.");
        sender.spigot().sendMessage(builder.build());
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
