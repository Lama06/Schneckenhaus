package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.config.ConfigException;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellGlobalConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        final Map<String, CustomShellGlobalConfig> customConfig = SchneckenPlugin.INSTANCE.getSchneckenConfig().custom;
        final String name = args[0];
        if (customConfig.containsKey(name)) {
            sender.spigot().sendMessage(new ComponentBuilder("This name is already taken: " + name).color(ChatColor.RED).build());
            return;
        }
        final BlockArea template = Require.blockArea(sender, Arrays.copyOfRange(args, 1, 7));
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

        final CustomShellGlobalConfig newShell = new CustomShellGlobalConfig(true, ingredients, template);
        try {
            newShell.verify();
        } catch (final ConfigException e) {
            sender.spigot().sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).build());
            return;
        }
        customConfig.put(name, newShell);

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
